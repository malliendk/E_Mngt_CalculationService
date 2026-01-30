package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.Tile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.sumBuildingProperty;


@Service
@AllArgsConstructor
@Slf4j
public class DistrictStatsCalculationService {

    private final PowerSystemService powerSystemService;

    /**
     * Calculates cumulative values for all districts in the game, including power system effects.
     *
     * @param districts The list of districts to process.
     * @return A list of fully processed Districts.
     */
    public List<District> calculateCumulativeDistrictValues(List<District> districts) {
        // Calculate basic district values (energy, gold, popularity, etc.)
        accumulateDistrictValues(districts);

        // Initialize power system and calculate power flows
        powerSystemService.initialize(districts);
        powerSystemService.calculatePowerFlows();
        log.info("Power system calculations completed for {} districts", districts.size());

        return districts;
    }

    /**
     * Accumulates basic values for each district from its buildings.
     *
     * @param districts The list of districts to process.
     */
    private void accumulateDistrictValues(List<District> districts) {
        for (District district : districts) {
            List<BuildingDTO> districtBuildings = district.getTiles().stream()
                    .map(Tile::getBuilding)
                    .toList();
            district.setEnergyProduction(sumBuildingProperty(BuildingDTO::getEnergyProduction, districtBuildings));
            district.setEnergyConsumption(sumBuildingProperty(BuildingDTO::getEnergyConsumption, districtBuildings));
            district.setGridCapacity(sumBuildingProperty(BuildingDTO::getGridCapacity, districtBuildings));
            district.setGoldIncome(sumBuildingProperty(BuildingDTO::getGoldIncome, districtBuildings));
            district.setPopularityIncome(sumBuildingProperty(BuildingDTO::getPopularityIncome, districtBuildings));
            district.setResearchIncome(sumBuildingProperty(BuildingDTO::getResearchIncome, districtBuildings));
            district.setEnvironmentalScore(sumBuildingProperty(BuildingDTO::getEnvironmentalScore, districtBuildings));
            district.setSolarPanelCapacity(sumBuildingProperty(BuildingDTO::getSolarPanelCapacity, districtBuildings));
            district.setSolarPanelAmount(sumBuildingProperty(BuildingDTO::getSolarPanelAmount, districtBuildings));
        }
    }
}




