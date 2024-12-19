package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class GameDtoBuilderService {

    private final CalculationHelperService calculationHelperService;
    private final BuildingUpdateService buildingUpdateService;

    public GameDTO setSolarPanels(GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings()
                .stream()
                .map(buildingDTO -> {
                            if (buildingDTO.getSolarPanelAmount() > 0) {
                                return buildingUpdateService.setSolarPanelToBuilding(buildingDTO);
                            }
                            return buildingDTO;
                        }
                )
                .toList();
        return gameDTO.toBuilder()
                .buildings(updatedBuildings)
                .build();
    }

    public GameDTO mapSolarIncome(GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings()
                .stream()
                .filter(buildingDTO -> buildingDTO.getSolarPanelSet() != null)
                .map(buildingUpdateService::mapSolarProductionToBuilding)
                .toList();
        double updatedGoldIncome = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getGoldIncome, updatedBuildings);
        double updatedResearchIncome = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getResearchIncome, updatedBuildings);
        double updatedEnvironmentalIncome = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnvironmentalIncome, updatedBuildings);
        return gameDTO.toBuilder()
                .goldIncome(updatedGoldIncome)
                .researchIncome(updatedResearchIncome)
                .environmentalIncome(updatedEnvironmentalIncome)
                .buildings(updatedBuildings)
                .build();
    }

    public GameDTO updateSolarPanelAmount(GameDTO gameDTO) {
        int updatedSolarPanelAmount = gameDTO.getBuildings()
                .stream()
                .mapToInt(BuildingDTO::getSolarPanelAmount)
                .sum();
        return gameDTO.toBuilder()
                .solarPanelTotalAmount(updatedSolarPanelAmount)
                .build();
    }

    public GameDTO updateSolarPanelCapacity(GameDTO gameDTO) {
        double updatedSolarPanelCapacity = gameDTO.getBuildings()
                .stream()
                .mapToInt(BuildingDTO::getSolarPanelCapacity)
                .sum();
        return gameDTO.toBuilder()
                .solarPanelCapacity(updatedSolarPanelCapacity)
                .build();
    }

    public GameDTO updateEnergyProductionByDayWeather(GameDTO gameDTO) {
        double updatedTotalEnergyProduction = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnergyProduction,
                gameDTO.getBuildings());
        return gameDTO.toBuilder()
                .energyProduction(updatedTotalEnergyProduction)
                .build();
    }

    public GameDTO updateEnergyConsumptionByDayWeather(GameDTO gameDTO) {
        double updatedTotalEnergyConsumption = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnergyConsumption, gameDTO.getBuildings());
        return gameDTO.toBuilder()
                .energyConsumption(updatedTotalEnergyConsumption)
                .build();
    }

    public GameDTO updateGridLoad(GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings().stream()
                .map(buildingUpdateService::updateGridLoad)
                .toList();
        double updatedGridLoad = updatedBuildings.stream()
                .mapToDouble(BuildingDTO::getGridLoad)
                .sum();
        return gameDTO.toBuilder()
                //.totalGridLoad(updatedGridLoad)
                .buildings(updatedBuildings)
                .build();
    }

    public GameDTO updateGridCapacity(final GameDTO gameDTO) {

        double updatedTotalGridCapacity = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getGridCapacity,
                gameDTO.getBuildings());
        return gameDTO.toBuilder()
                .gridCapacity(updatedTotalGridCapacity)
                .build();
    }

    public GameDTO updateEnergyProductionByDayWeather(FactorProvider timeOrWeather, GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings().stream()
                .map(buildingDTO -> buildingUpdateService.updateEnergyProduction(timeOrWeather, buildingDTO))
                .toList();
        double updatedProductionTotal = updatedBuildings.stream()
                .mapToDouble(BuildingDTO::getEnergyProduction)
                .sum();
        return gameDTO.toBuilder()
                .energyProduction(updatedProductionTotal)
                .buildings(updatedBuildings)
                .build();
    }

    public GameDTO updateEnergyConsumptionByDayWeather(TimeOfDay timeOfDay, GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings().stream()
                .map(mapToCorrectConsuption(timeOfDay))
                .toList();
        double updatedConsumptionTotal = calculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnergyConsumption,
                updatedBuildings);
        return gameDTO.toBuilder()
                .energyConsumption(updatedConsumptionTotal)
                .buildings(updatedBuildings)
                .build();
    }

    private Function<BuildingDTO, BuildingDTO> mapToCorrectConsuption(final TimeOfDay timeOfDay) {
        return building -> {
            if (building.getHouseHolds() > 0) {
                return buildingUpdateService.updateHousingEnergyConsumption(timeOfDay, building);
            } else if (building.getGoldIncome() > 0) {
                return buildingUpdateService.updateIndustrialConsumption(timeOfDay, building);
            } else {
                return building;
            }
        };
    }


    public GameDTO addIncome(GameDTO gameDTO) {
        GameDTO updatedGameDTO = gameDTO.toBuilder()
                .funds(gameDTO.getFunds() + gameDTO.getGoldIncome())
                .popularity(gameDTO.getPopularity() + gameDTO.getPopularityIncome())
                .research(gameDTO.getResearch() + gameDTO.getResearchIncome())
                .environmentalScore(gameDTO.getEnvironmentalScore() + gameDTO.getEnvironmentalIncome())
                .build();
        log.info("updated gameDTO: {}", updatedGameDTO);
        return updatedGameDTO;
    }
}
