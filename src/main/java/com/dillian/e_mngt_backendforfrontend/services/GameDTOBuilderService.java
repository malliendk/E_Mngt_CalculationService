package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GameDTOBuilderService {

    public void mapSolarProduction(List<BuildingDTO> buildings) {
        buildings.forEach(building -> {
            CalculationHelperService.mapSolarProduction(buildings, SolarPanelSetDTO::getEnergyProduction,
                    BuildingDTO::setEnergyProduction);
            CalculationHelperService.mapSolarProduction(buildings, SolarPanelSetDTO::getGoldIncome,
                    BuildingDTO::setEnergyProduction);
            CalculationHelperService.mapSolarProduction(buildings, SolarPanelSetDTO::getResearchIncome,
                    BuildingDTO::setEnergyProduction);
            CalculationHelperService.mapSolarProduction(buildings, SolarPanelSetDTO::getEnvironmentIncome,
                    BuildingDTO::setEnergyProduction);
        });
    }

    public GameDTO calculateBasicStats(GameDTO gameDTO) {
        GameDTO.GameDTOBuilder gameDTOBuilder = new GameDTO.GameDTOBuilder().updateValues(gameDTO.getBuildings());
        return gameDTOBuilder.build(gameDTO.getBuildings(), gameDTO.getSupervisor());
    }
}
