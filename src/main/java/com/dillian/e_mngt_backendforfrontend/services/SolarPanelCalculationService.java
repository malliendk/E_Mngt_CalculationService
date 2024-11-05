package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.StatsCalculationHelperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SolarPanelCalculationService {

    private final StatsCalculationHelperService helperService;

    public void mapSolarProduction(List<BuildingDTO> buildings) {
        buildings.forEach(building -> {
            helperService.mapSolarProduction(SolarPanelSetDTO::getEnergyProduction, BuildingDTO::getEnergyProduction,
                    BuildingDTO::setEnergyProduction, building);
            helperService.mapSolarProduction(SolarPanelSetDTO::getGoldIncome, BuildingDTO::getGoldIncome,
                    BuildingDTO::setGoldIncome, building);
            helperService.mapSolarProduction(SolarPanelSetDTO::getResearchIncome, BuildingDTO::getResearchIncome,
                    BuildingDTO::setResearchIncome, building);
            helperService.mapSolarProduction(SolarPanelSetDTO::getEnvironmentIncome, BuildingDTO::getEnvironmentalIncome,
                    BuildingDTO::setEnvironmentalIncome, building);
        });
    }

    public void updateSolarPanelProduction(TimeOfDay timeOfDay, GameDTO gameDTO) {
        double generationFactorDayTime = timeOfDay.getGenerationFactor();
        gameDTO.getBuildings()
                .stream()
                .map(BuildingDTO::getSolarPanelSets)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(solarSet -> solarSet.setEnergyProduction(solarSet.getEnergyProduction() + generationFactorDayTime));
    }

    public void updateSolarPanelProduction(WeatherType weatherType, GameDTO gameDTO) {
        double generationFactorWeatherType = weatherType.getGenerationFactor();
        gameDTO.getBuildings()
                .stream()
                .map(BuildingDTO::getSolarPanelSets)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(solarSet ->
                        solarSet.setEnergyProduction(solarSet.getEnergyProduction() * generationFactorWeatherType));
    }

    public void sumSolarPanelProductionByBuilding(GameDTO gameDTO) {
        double newTotalProduction = gameDTO.getBuildings()
                .stream()
                .filter(buildingDTO -> buildingDTO.getSolarPanelSets() != null)
                .mapToDouble(buildingDTO -> {
                    double newProduction = buildingDTO.getSolarPanelSets()
                            .stream()
                            .mapToDouble(SolarPanelSetDTO::getEnergyProduction)
                            .sum();
                    buildingDTO.setEnergyProduction(newProduction);
                    return newProduction;
                })
                .sum();
        gameDTO.setEnergyProduction(newTotalProduction);
    }
}
