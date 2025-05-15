package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dillian.e_mngt_backendforfrontend.services.utils.CalculationHelperService.sumBuildingProperty;

@Service
@Slf4j
public class DistrictStatsCalculationService {

    /**
     * Calculates cumulative values for all districts in the game.
     * @param districts The game data transfer object containing all districts.
     * @return a list of fully processed Districts.
     */
    public List<District> calculateCumulativeDistrictValues(List<District> districts) {
        calculateIndividualDistrictValues(districts);
        return processExcessBalance(districts);
    }

    /**
     * Processes the excess balance for each district by distributing it to adjacent districts and calculating final balances and grid loads.
     * @param districts The game data transfer object containing all districts.
     * @return a list of Districts with their property values fully calculated
     */
    public List<District> processExcessBalance(List<District> districts) {

                // Map each district to its position in the grid
        Map<District, Point> districtPositions = mapDistrictsToPositions(districts);

        // First, distribute excess energy to adjacent districts
        for (District currentDistrict : districts) {
            final int excessToShare = currentDistrict.getExcessBalance();

            // Find adjacent districts
            List<District> adjacentDistricts = findAdjacentDistricts(currentDistrict, districts, districtPositions);

            if (!adjacentDistricts.isEmpty()) {
                final int sharePerDistrict = excessToShare / adjacentDistricts.size();

                for (District adjacentDistrict : adjacentDistricts) {
                    adjacentDistrict.getIncomingExcessBalances().add(sharePerDistrict);
                }
            }
        }

        // Then calculate final balances and grid loads
        for (District district : districts) {
            int totalIncomingExcess = district.getIncomingExcessBalances().stream().mapToInt(Integer::intValue).sum();
            int finalExcessBalance = district.getExcessBalance() + totalIncomingExcess;

            district.setExcessBalance(finalExcessBalance);

            // Avoid division by zero
            if (district.getGridCapacity() > 0) {
                final double gridLoad = (double) finalExcessBalance / district.getGridCapacity();
                district.setGridLoad(gridLoad);
            } else {
                district.setGridLoad(0);
            }
        }
        return districts;
    }

    /**
     * Maps each district to its position in the grid.
     * Districts are arranged in a 2x2 grid initially, with additional rows of 2 added below.
     * @param districts The list of districts to be mapped.
     * @return A map of districts to their positions in the grid.
     */
    private Map<District, Point> mapDistrictsToPositions(List<District> districts) {
        Map<District, Point> positions = new HashMap<>();
        for (int i = 0; i < districts.size(); i++) {
            int x = i % 2;      // 0 for left column, 1 for right column
            int y = i / 2;      // Row number (0-indexed)
            positions.put(districts.get(i), new Point(x, y));
        }
        return positions;
    }

    /**
     * Finds districts that are adjacent to the given district.
     * Districts are adjacent if they share an edge (not including corners).
     * @param current The current district.
     * @param allDistricts The list of all districts.
     * @param positions The map of districts to their positions in the grid.
     * @return A list of adjacent districts.
     */
    private List<District> findAdjacentDistricts(District current, List<District> allDistricts,
                                                 Map<District, Point> positions) {
        Point currentPos = positions.get(current);
        return allDistricts.stream()
                .filter(other -> other != current)
                .filter(other -> {
                    Point otherPos = positions.get(other);

                    // Two districts are adjacent if they differ by 1 in exactly one coordinate
                    // and are the same in the other coordinate (sharing an edge)
                    int xDiff = Math.abs(currentPos.x - otherPos.x);
                    int yDiff = Math.abs(currentPos.y - otherPos.y);

                    return (xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates individual values for each district, such as energy production, energy consumption, and grid capacity.
     * @param districts The game data transfer object containing all districts.
     */
    private void calculateIndividualDistrictValues(List<District> districts) {
        for (District district : districts) {
            List<BuildingDTO> districtBuildings = district.getTiles().stream().map(Tile::getBuilding).toList();
            log.info("district buildings: {}", districtBuildings);

            final int energyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, districtBuildings);
            final int energyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, districtBuildings);
            final int gridCapacity = sumBuildingProperty(BuildingDTO::getGridCapacity, districtBuildings);

            district.setEnergyProduction(energyProduction);
            district.setEnergyConsumption(energyConsumption);
            district.setGridCapacity(gridCapacity);
            district.setExcessBalance(energyProduction - energyConsumption);

            district.setIncomingExcessBalances(new ArrayList<>());
        }
    }
}
