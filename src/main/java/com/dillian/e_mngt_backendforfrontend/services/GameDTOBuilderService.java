package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.SaveGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.services.calculations.DistrictStatsCalculationService;
import com.dillian.e_mngt_backendforfrontend.services.calculations.IncomeLossCalculator;
import com.dillian.e_mngt_backendforfrontend.utils.constants.StartingValues;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.calculateGridLoad;
import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.sumBuildingProperty;

@Service
@AllArgsConstructor
@Slf4j
public class GameDTOBuilderService {

    private final BuildingService buildingService;
    private final SupervisorService supervisorService;
    private final DistrictStatsCalculationService districtStatsCalculationService;
    private final IncomeLossCalculator incomeLossCalculator;

      public InitiateDTO mapToInitiateDTO(SaveGameDTO saveGameDTO) {
        return new InitiateDTO(
                saveGameDTO.getId(),
                saveGameDTO.getSupervisor(),
                saveGameDTO.getTiles(),
                saveGameDTO.getDistricts(),
                saveGameDTO.getBuildingRequests(),
                saveGameDTO.getFunds(),
                saveGameDTO.getPopularity(),
                saveGameDTO.getResearch(),
                saveGameDTO.getEnvironmentalScore()
        );
    }

    public ExtendedGameDTO extendToGameDTO(InitiateDTO initiateDTO) {
        final List<BuildingDTO> populatedBuildings = buildingService.getBuildingsById(initiateDTO);
        final int energyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, populatedBuildings);
        final int energyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, populatedBuildings);
        final int gridCapacity = sumBuildingProperty(BuildingDTO::getGridCapacity, populatedBuildings);
        final String startingTimeOfDay = StartingValues.TIME_OF_DAY_STARTING_VALUE;
        final String startingWeatherType = StartingValues.WEATHER_TYPE_STARTING_VALUE;
        final double gridLoad = calculateGridLoad(energyProduction, energyConsumption, gridCapacity, initiateDTO.getSupervisor());
        initiateDTO = buildingService.assignTilesToDistricts(initiateDTO, populatedBuildings);
        final List<District> processedDistricts = districtStatsCalculationService.calculateCumulativeDistrictValues(initiateDTO.getDistricts());
        ExtendedGameDTO dto = ExtendedGameDTO.builder()
                .id(initiateDTO.getId())
                .funds(initiateDTO.getFunds())
                .popularity(initiateDTO.getPopularity())
                .research(initiateDTO.getResearch())
                .buildings(populatedBuildings)
                .energyProduction(energyProduction)
                .energyConsumption(energyConsumption)
                .gridCapacity(gridCapacity)
                .gridLoad(gridLoad)
                .solarPanelAmount(sumBuildingProperty(BuildingDTO::getSolarPanelAmount, populatedBuildings))
                .solarPanelCapacity(sumBuildingProperty(BuildingDTO::getSolarPanelCapacity, populatedBuildings))
                .housing(sumBuildingProperty(BuildingDTO::getHousing, populatedBuildings))
                .goldIncome(supervisorService.processGoldIncome(initiateDTO, populatedBuildings))
                .popularityIncome(supervisorService.processPopularityIncome(initiateDTO, populatedBuildings))
                .researchIncome(supervisorService.processResearchIncome(initiateDTO, populatedBuildings))
                .environmentalScore(sumBuildingProperty(BuildingDTO::getEnvironmentalScore, populatedBuildings))
                .timeOfDay(startingTimeOfDay)
                .weatherType(startingWeatherType)
                .districts(processedDistricts)
                .supervisor(initiateDTO.getSupervisor())
                .build();
        log.info("retrieved supervisor: " + initiateDTO.getSupervisor());
        return dto;
    }
}
