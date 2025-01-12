package com.dillian.e_mngt_backendforfrontend.services;


import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GameDTOBuilderService {


    private final DayWeatherService dayWeatherService;

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

    public GameDTO updateStats(GameDTO gameDTO) {
        GameDTO.GameDTOBuilder gameDTOBuilder = new GameDTO.GameDTOBuilder().updateValues(gameDTO.getBuildings());
        return gameDTOBuilder.build(gameDTO.getBuildings(), gameDTO.getSupervisor());
    }

    public GameDTO updateDTOByTimeOfDay(GameDTO gameDTO) {
        //update of gameDTO is done by directly summing the buildingDTOs' production and consumption within its stream
        //the production and consumption values of the buildingDTOs are not persisted
        final List<BuildingDTO> buildings = gameDTO.getBuildings();
        final TimeOfDay timeOfDay = dayWeatherService.cycleThroughTimesOfDay();
        double newEnergyProduction = CalculationHelperService.updateByDayOrWeather(
                buildings, BuildingDTO::getEnergyProduction, timeOfDay.getGenerationFactor());
        double newHousingConsumption = CalculationHelperService.updateByDayOrWeather(
                buildings, BuildingDTO::getEnergyConsumption, timeOfDay.getHousingConsumptionFactor());
        double newIndustrialConsumption = CalculationHelperService.updateByDayOrWeather(
                buildings, BuildingDTO::getEnergyConsumption, timeOfDay.getIndustrialConsumptionFactor());
        gameDTO.setEnergyProduction(newEnergyProduction);
        gameDTO.setEnergyConsumption(newHousingConsumption + newIndustrialConsumption);
        gameDTO.setTimeOfDay(timeOfDay);
        return gameDTO;
    }

    public GameDTO updateDTOByWeatherType(GameDTO gameDTO) {
        //update of gameDTO is done by directly summing the buildingDTOs' production and consumption within its stream
        //the production values of the buildingDTOs are not persisted
        WeatherType newWeatherType = dayWeatherService.getRandomWeatherType();
        double newEnergyProduction = CalculationHelperService.updateByDayOrWeather(
                gameDTO.getBuildings(), BuildingDTO::getEnergyProduction, newWeatherType.getGenerationFactor());
        gameDTO.setEnergyProduction(newEnergyProduction);
        gameDTO.setWeatherType(newWeatherType);
        return gameDTO;
    }
}
