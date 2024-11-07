package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GameDtoBuilderService {

    private final CalculationHelperService calculationHelperService;
    private final BuildingUpdateService buildingUpdateService;


    public GameDTO mapSolarIncome(GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings()
                .stream()
                .map(buildingUpdateService::mapSolarProductionToBuilding)
                .toList();
        double updatedGoldIncome = calculationHelperService.sumBuildingProperty(BuildingDTO::getGoldIncome, updatedBuildings);
        double updatedResearchIncome = calculationHelperService.sumBuildingProperty(BuildingDTO::getResearchIncome, updatedBuildings);
        double updatedEnvironmentalIncome = calculationHelperService.sumBuildingProperty(BuildingDTO::getEnvironmentalIncome, updatedBuildings);
        return gameDTO.toBuilder()
                .buildings(updatedBuildings)
                .goldIncome(updatedGoldIncome)
                .researchIncome(updatedResearchIncome)
                .environmentalIncome(updatedEnvironmentalIncome)
                .build();
    }

    public GameDTO updateEnergyProduction(FactorProvider factorProvider, GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings().stream()
                .map(building -> {
                    if (building.getSolarPanelSets() != null) {
                        return buildingUpdateService.updateSolarBuildingProduction(factorProvider, building);
                    } else if (building.getEnergyProduction() > 0) {
                        return buildingUpdateService.updateEnergySourceProduction(factorProvider, building);
                    } else {
                        return building;
                    }
                })
                .toList();
        double updatedProductionTotal = calculationHelperService.sumBuildingProperty(BuildingDTO::getEnergyProduction,
                updatedBuildings);
        return gameDTO.toBuilder()
                .energyProduction(updatedProductionTotal)
                .buildings(updatedBuildings)
                .build();
    }

    public GameDTO updateEnergyConsumption(FactorProvider factorProvider, GameDTO gameDTO) {
        List<BuildingDTO> updatedBuildings = gameDTO.getBuildings().stream()
                .map(building -> {
                    if (building.getHouseHolds() > 0) {
                        return buildingUpdateService.updateHousingConsumption(factorProvider, building);
                    } else if (building.getGoldIncome() > 0) {
                        return buildingUpdateService.updateIndustrialConsumption(factorProvider, building);
                    } else {
                        return building;
                    }
                })
                .toList();
        double updatedConsumptionTotal = calculationHelperService.sumBuildingProperty(BuildingDTO::getEnergyConsumption,
                updatedBuildings);
        return gameDTO.toBuilder()
                .energyConsumption(updatedConsumptionTotal)
                .buildings(updatedBuildings)
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
                .totalGridLoad(updatedGridLoad)
                .buildings(updatedBuildings)
                .build();
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
