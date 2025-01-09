package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Slf4j
public class GameService {

    private GameDTO gameDTO;

    private final DayWeatherService dayWeatherService;
    private final GameDTOBuilderService gameDTOBuilderService;
    private final CalculationHelperService calculationHelperService;

    public GameService(CalculationHelperService calculationHelperService, GameDTOBuilderService gameDTOBuilderService, final DayWeatherService dayWeatherService) {
        this.calculationHelperService = calculationHelperService;
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.dayWeatherService = dayWeatherService;
    }

    public void buildGameDTO(GameDTO gameDTO) {
        //accumulated income from solar panels is mapped onto their holding buildings
        //then gameDTO value are updated by the summed values of all buildings' properties
        gameDTOBuilderService.mapSolarProduction(gameDTO.getBuildings());
        this.gameDTO = gameDTOBuilderService.calculateBasicStats(gameDTO);
    }

    public void updateDtoByTimeOfDay(TimeOfDay timeOfDay) {
        //update of gameDTO is done by directly summing the buildingDTOs' production and consumption within its stream
        //the production and consumption values of the buildingDTOs are not persisted
        final List<BuildingDTO> housingBuildings = this.gameDTO.getBuildings().stream()
                .filter(building -> building.getHouseHolds() > 0)
                .toList();
        final List<BuildingDTO> industrialBuildings = this.gameDTO.getBuildings().stream()
                .filter(building -> building.getGoldIncome() > 0)
                .toList();
        double newEnergyProduction = CalculationHelperService.updateByDayOrWeather(
                housingBuildings, BuildingDTO::getEnergyProduction, timeOfDay.getGenerationFactor());
        double newHousingConsumption = CalculationHelperService.updateByDayOrWeather(
                industrialBuildings, BuildingDTO::getEnergyConsumption, timeOfDay.getHousingConsumptionFactor());
        double newIndustrialConsumption = CalculationHelperService.updateByDayOrWeather(
                industrialBuildings, BuildingDTO::getEnergyConsumption, timeOfDay.getIndustrialConsumptionFactor());
        this.gameDTO.setEnergyProduction(newEnergyProduction);
        this.gameDTO.setEnergyConsumption(newHousingConsumption + newIndustrialConsumption);
        this.gameDTO.setTimeOfDay(timeOfDay);
    }

    public void updateDtoByWeatherType(WeatherType weatherType) {
        //update of gameDTO is done by directly summing the buildingDTOs' production and consumption within its stream
        //the production values of the buildingDTOs are not persisted
        double newEnergyProduction = CalculationHelperService.updateByDayOrWeather(
                this.gameDTO.getBuildings(), BuildingDTO::getEnergyProduction, weatherType.getGenerationFactor());
        this.gameDTO.setEnergyProduction(newEnergyProduction);
        this.gameDTO.setWeatherType(weatherType);
    }

    public void addIncome(GameDTO gameDTO) {
        gameDTO.setFunds(gameDTO.getFunds() + gameDTO.getGoldIncome());
        gameDTO.setPopularity(gameDTO.getPopularity() + gameDTO.getPopularityIncome());
        gameDTO.setResearch(gameDTO.getResearch() + gameDTO.getResearchIncome());
        gameDTO.setEnvironmentalScore(gameDTO.getEnvironmentalScore() + gameDTO.getEnvironmentalIncome());
        log.info("updated gameDTO: {}", gameDTO);
    }
}

