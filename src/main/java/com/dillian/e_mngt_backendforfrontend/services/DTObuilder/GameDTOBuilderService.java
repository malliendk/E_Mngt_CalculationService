package com.dillian.e_mngt_backendforfrontend.services.DTObuilder;

import com.dillian.e_mngt_backendforfrontend.constants.StartingValues;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dillian.e_mngt_backendforfrontend.services.DTObuilder.CalculationHelperService.mapSolarProduction;
import static com.dillian.e_mngt_backendforfrontend.services.DTObuilder.CalculationHelperService.sumBuildingProperty;

@Service
@AllArgsConstructor
public class GameDTOBuilderService {

    private final BuildingRetrieveService buildingRetrieveService;

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
        final List<BuildingDTO> initiateBuildings = buildingRetrieveService.getBuildingsById(initiateDTO);
        final List<BuildingDTO> buildingsWithSolarPanels = addSolarPanelsToBuildings(initiateDTO, initiateBuildings);
        final List<BuildingDTO> fullyProcessedBuildings = updateBuildingsWithSolarPanelScore(buildingsWithSolarPanels);
        return calculateStats(initiateDTO, fullyProcessedBuildings);
    }

    private ExtendedGameDTO calculateStats(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        int energyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, fullyProcessedBuildings);
        int energyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, fullyProcessedBuildings);
        int gridCapacity = sumBuildingProperty(BuildingDTO::getGridCapacity, fullyProcessedBuildings);
        String startingTimeOfDay = StartingValues.TIME_OF_DAY_STARTING_VALUE;
        String startingWeatherType = StartingValues.WEATHER_TYPE_STARTING_VALUE;
        double gridLoad = (double)(energyProduction - energyConsumption) / gridCapacity;
        return ExtendedGameDTO.builder()
                .id(initiateDTO.getId())
                .funds(initiateDTO.getFunds())
                .popularity(initiateDTO.getPopularity())
                .research(initiateDTO.getResearch())
                .environmentalScore(initiateDTO.getEnvironmentalScore())
                .buildings(fullyProcessedBuildings)
                .energyProduction(energyProduction)
                .energyConsumption(energyConsumption)
                .gridCapacity(gridCapacity)
                .gridLoad(gridLoad)
                .solarPanelAmount(sumBuildingProperty(BuildingDTO::getSolarPanelAmount, fullyProcessedBuildings))
                .solarPanelCapacity(sumBuildingProperty(BuildingDTO::getSolarPanelCapacity, fullyProcessedBuildings))
                .goldIncome(sumBuildingProperty(BuildingDTO::getGoldIncome, fullyProcessedBuildings))
                .researchIncome(sumBuildingProperty(BuildingDTO::getResearchIncome, fullyProcessedBuildings))
                .popularityIncome(sumBuildingProperty(BuildingDTO::getPopularityIncome, fullyProcessedBuildings))
                .timeOfDay(startingTimeOfDay)
                .weatherType(startingWeatherType)
                .build();
    }

    private List<BuildingDTO> updateBuildingsWithSolarPanelScore(List<BuildingDTO> buildings) {
        buildings.stream()
                .filter(building -> building.getSolarPanelSet() != null)
                .forEach(building -> {
                    mapSolarProduction(building, SolarPanelSetDTO::getEnergyProduction,
                            BuildingDTO::setEnergyProduction);
                    mapSolarProduction(building, SolarPanelSetDTO::getGoldIncome,
                            BuildingDTO::setGoldIncome);
                    mapSolarProduction(building, SolarPanelSetDTO::getResearchIncome,
                            BuildingDTO::setResearchIncome);
                    mapSolarProduction(building, SolarPanelSetDTO::getEnvironmentIncome,
                            BuildingDTO::setEnvironmentalIncome);
                });
        return buildings;
    }

    private List<BuildingDTO> addSolarPanelsToBuildings(InitiateDTO initiateDTO, List<BuildingDTO> initiateBuildings) {
        final Map<Long, BuildingDTO> buildingIdMap = initiateBuildings.stream()
                .collect(Collectors.toMap(BuildingDTO::getId, Function.identity()));
        for (BuildingRequestDTO buildingRequest : initiateDTO.getBuildingRequests()) {
            final BuildingDTO building = buildingIdMap.get(buildingRequest.getBuildingId());
            if (building != null) {
                building.setSolarPanelAmount(buildingRequest.getSolarPanelAmount());
            }
        }
        return initiateBuildings;
    }
}
