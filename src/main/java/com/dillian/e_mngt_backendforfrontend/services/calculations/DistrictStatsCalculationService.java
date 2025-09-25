package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.*;
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
     * Calculates cumulative values for all districts in the game.
     * @param districts The list of districts to process.
     * @return a list of fully processed Districts.
     */
    public List<District> calculateCumulativeDistrictValues(List<District> districts) {
        accumulateDistrictValues(districts);
        powerSystemService.initialize(districts);
        powerSystemService.calculatePowerFlows();
        return districts;
    }

    private void accumulateDistrictValues(List<District> districts) {
        for (District district : districts) {
            List<BuildingDTO> districtBuildings = district.getTiles().stream().map(Tile::getBuilding).toList();
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




