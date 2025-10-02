package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.services.calculations.DistrictStatsCalculationService;
import com.dillian.e_mngt_backendforfrontend.services.calculations.IncomeLossCalculator;
import com.dillian.e_mngt_backendforfrontend.utils.constants.StartingValues;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.*;

@Service
@AllArgsConstructor
@Slf4j
public class GameDTOBuilderService {

    private final BuildingService buildingService;
    private final SupervisorService supervisorService;
    private final DistrictStatsCalculationService districtStatsCalculationService;
    private final IncomeLossCalculator incomeLossCalculator;

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
        final List<BuildingDTO> populatedBuildings = buildingService.getBuildingsById(initiateDTO);
        return calculateStats(initiateDTO, populatedBuildings);
    }

    private ExtendedGameDTO calculateStats(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        final int energyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, fullyProcessedBuildings);
        final int energyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, fullyProcessedBuildings);
        final int gridCapacity = sumBuildingProperty(BuildingDTO::getGridCapacity, fullyProcessedBuildings);
        final String startingTimeOfDay = StartingValues.TIME_OF_DAY_STARTING_VALUE;
        final String startingWeatherType = StartingValues.WEATHER_TYPE_STARTING_VALUE;
        final double gridLoad = calculateGridLoad(energyProduction, energyConsumption, gridCapacity, initiateDTO.getSupervisor());
        initiateDTO = buildingService.assignTilesToDistricts(initiateDTO, fullyProcessedBuildings);
        final List<District> processedDistricts = districtStatsCalculationService.calculateCumulativeDistrictValues(initiateDTO.getDistricts());
        ExtendedGameDTO dto = ExtendedGameDTO.builder()
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
                .housing(sumBuildingProperty(BuildingDTO::getHousing, fullyProcessedBuildings))
                .goldIncome(supervisorService.processGoldIncome(initiateDTO, fullyProcessedBuildings))
                .popularityIncome(supervisorService.processPopularityIncome(initiateDTO, fullyProcessedBuildings))
                .researchIncome(supervisorService.processResearchIncome(initiateDTO, fullyProcessedBuildings))
                .environmentalScore(sumBuildingProperty(BuildingDTO::getEnvironmentalScore, fullyProcessedBuildings))
                .timeOfDay(startingTimeOfDay)
                .weatherType(startingWeatherType)
                .districts(processedDistricts)
                .supervisor(initiateDTO.getSupervisor())
                .build();
        log.info("retrieved supervisor: " + initiateDTO.getSupervisor());
        return dto;
    }
}
