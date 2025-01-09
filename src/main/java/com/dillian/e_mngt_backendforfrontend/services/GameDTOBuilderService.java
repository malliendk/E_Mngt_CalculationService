package com.dillian.e_mngt_backendforfrontend.services;


import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@Slf4j
public class GameDTOBuilderService {

    public void mapSolarProduction(List<BuildingDTO> buildings) {
                buildings.stream()
                .filter(building -> building.getSolarPanelSet() != null)
                .forEach(building -> {
                    CalculationHelperService.mapSolarProduction(building, SolarPanelSetDTO::getEnergyProduction,
                            BuildingDTO::setEnergyProduction);
                    CalculationHelperService.mapSolarProduction(building, SolarPanelSetDTO::getGoldIncome,
                            BuildingDTO::setGoldIncome);
                    CalculationHelperService.mapSolarProduction(building, SolarPanelSetDTO::getResearchIncome,
                            BuildingDTO::setResearchIncome);
                    CalculationHelperService.mapSolarProduction(building, SolarPanelSetDTO::getEnvironmentIncome,
                            BuildingDTO::setEnvironmentalIncome);
                });
    }

    public GameDTO calculateBasicStats(GameDTO gameDTO) {
        GameDTO.GameDTOBuilder gameDTOBuilder = new GameDTO.GameDTOBuilder().updateValues(gameDTO.getBuildings());
        return gameDTOBuilder.build(gameDTO.getBuildings(), gameDTO.getSupervisor());
    }
}
