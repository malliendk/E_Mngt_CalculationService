package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.services.SolarPanelCalculationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GameDTOBuilderService {

    private final StatsCalculationHelperService helperService;
    private final SolarPanelCalculationService solarPanelCalculationService;


    public GameDTO buildBasicDTO(GameDTO gameDTO) {
        solarPanelCalculationService.mapSolarProduction(gameDTO.getBuildings()
                .stream()
                .filter(buildingDTO -> buildingDTO.getSolarPanelSets() != null)
                .toList());
        List<BuildingDTO> buildings = gameDTO.getBuildings();
        gameDTO.setTotalGridLoad(helperService.sumDoubleProperty(buildings, BuildingDTO::getGridLoad));
        gameDTO.setGridCapacity(helperService.sumIntProperty(buildings, BuildingDTO::getGridCapacity));
        gameDTO.setHouseholds(helperService.sumIntProperty(buildings, BuildingDTO::getHouseHolds));
        gameDTO.setEnergyConsumption(helperService.sumDoubleProperty(buildings, BuildingDTO::getEnergyConsumption));
        gameDTO.setEnergyProduction(helperService.sumDoubleProperty(buildings, BuildingDTO::getEnergyProduction));
        gameDTO.setGoldIncome(helperService.sumDoubleProperty(buildings, BuildingDTO::getGoldIncome));
        gameDTO.setResearchIncome(helperService.sumDoubleProperty(buildings, BuildingDTO::getResearchIncome));
        gameDTO.setPopularityIncome(helperService.sumIntProperty(buildings, BuildingDTO::getPopularityIncome));
        return gameDTO;
    }
}
