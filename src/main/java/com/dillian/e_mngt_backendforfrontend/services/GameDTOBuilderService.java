package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.services.calculations.DistrictStatsCalculationService;
import com.dillian.e_mngt_backendforfrontend.services.utils.constants.StartingValues;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dillian.e_mngt_backendforfrontend.services.utils.CalculationHelperService.*;

@Service
@AllArgsConstructor
@Slf4j
public class GameDTOBuilderService {

    private final BuildingService buildingService;
    private final DistrictStatsCalculationService districtStatsCalculationService;

    /**
     * Builds and returns a GameDTO by processing buildings and accumulating income from solar panels.
     * <p>
     * The accumulated income from solar panels is mapped onto their respective holding buildings.
     * Then, the GameDTO values are updated by summing the properties of all buildings.
     *
     * @param initiateDTO The DTO containing initialization parameters.
     * @return The constructed GameDTO with updated values.
     */
    public ExtendedGameDTO buildGameDTO(InitiateDTO initiateDTO) {
        final List<BuildingDTO> populatedBuildings = buildingService.retrieveAndPopulateBuildings(initiateDTO);
        return calculateStats(initiateDTO, populatedBuildings);
    }

    private ExtendedGameDTO calculateStats(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        int energyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, fullyProcessedBuildings);
        int energyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, fullyProcessedBuildings);
        int gridCapacity = sumBuildingProperty(BuildingDTO::getGridCapacity, fullyProcessedBuildings);
        String startingTimeOfDay = StartingValues.TIME_OF_DAY_STARTING_VALUE;
        String startingWeatherType = StartingValues.WEATHER_TYPE_STARTING_VALUE;
        double gridLoad = calculateGridLoad(energyProduction, energyConsumption, gridCapacity);
        initiateDTO = buildingService.assignTilesToDistricts(initiateDTO, fullyProcessedBuildings);
        List<District> processedDistricts = districtStatsCalculationService.calculateCumulativeDistrictValues(initiateDTO.getDistricts());
        log.info("districts: " + processedDistricts);
        int envScore = sumBuildingProperty(BuildingDTO::getEnvironmentalScore, fullyProcessedBuildings);
        log.info("envScore: " + envScore);
        return ExtendedGameDTO.builder()
                .id(initiateDTO.getId())
                .funds(initiateDTO.getFunds())
                .popularity(initiateDTO.getPopularity())
                .research(initiateDTO.getResearch())
                .buildings(fullyProcessedBuildings)
                .energyProduction(energyProduction)
                .energyConsumption(energyConsumption)
                .gridCapacity(gridCapacity)
                .gridLoad(gridLoad)
                .solarPanelAmount(sumBuildingProperty(BuildingDTO::getSolarPanelAmount, fullyProcessedBuildings))
                .solarPanelCapacity(sumBuildingProperty(BuildingDTO::getSolarPanelCapacity, fullyProcessedBuildings))
                .households(sumBuildingProperty(BuildingDTO::getHouseHolds, fullyProcessedBuildings))
                .goldIncome(sumBuildingProperty(BuildingDTO::getGoldIncome, fullyProcessedBuildings))
                .researchIncome(sumBuildingProperty(BuildingDTO::getResearchIncome, fullyProcessedBuildings))
                .popularityIncome(sumBuildingProperty(BuildingDTO::getPopularityIncome, fullyProcessedBuildings))
                .environmentalScore(sumBuildingProperty(BuildingDTO::getEnvironmentalScore, fullyProcessedBuildings))
                .timeOfDay(startingTimeOfDay)
                .weatherType(startingWeatherType)
                .districts(processedDistricts)
                .build();
    }
}
