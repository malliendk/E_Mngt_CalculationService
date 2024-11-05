package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.SolarPanelCalculationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BuildingStatsUpdateService {

    private final StatsCalculationHelperService helperService;
    private final SolarPanelCalculationService solarPanelCalculationService;



    public GameDTO addIncome(GameDTO gameDTO) {
        gameDTO.setFunds(gameDTO.getFunds() + gameDTO.getGoldIncome());
        gameDTO.setPopularity(gameDTO.getPopularity() + gameDTO.getPopularityIncome());
        gameDTO.setResearch(gameDTO.getResearch() + gameDTO.getResearchIncome());
        gameDTO.setEnvironmentalScore(gameDTO.getEnvironmentalScore() + gameDTO.getEnvironmentalIncome());
        log.info("updated gameDTO: {}", gameDTO);
        return gameDTO;
    }

    public void updateEnergySourceProduction(WeatherType weatherType, GameDTO gameDTO) {
        double generationFactor = weatherType.getGenerationFactor();
        gameDTO.getBuildings()
                .stream()
                .filter(building -> building.getEnergyProduction() != 0 && building.getSolarPanelSets() == null)
                .forEach(building -> building.setEnergyProduction(building.getEnergyProduction() * generationFactor));
    }

    public GameDTO updateProductionByTimeOfDay(TimeOfDay timeOfDay, GameDTO gameDTO) {
        gameDTO.setTimeOfDay(timeOfDay);

        return gameDTO;
    }

    public GameDTO updateGameByWeatherType(WeatherType weatherType, GameDTO gameDTO) {
        gameDTO.setWeatherType(weatherType);
        gameDTO.getBuildings()
                .forEach(building -> {
                    double newProduction = building.getEnergyProduction() * weatherType.getGenerationFactor();
                    building.setEnergyProduction(newProduction);
                });
        return gameDTO;
    }
//
//    public void updateFromTimeOfDay(TimeOfDay timeOfDay, GameDTO gameDTO) {
//        updateProductionByDayTime(timeOfDay, gameDTO);
//        updateHouseholdConsumption(timeOfDay, gameDTO);
//        updateIndustrialConsumption(timeOfDay, gameDTO);
//    }
//
//    private void updateProductionByDayTime(TimeOfDay timeOfDay, GameDTO gameDTO) {
//        helperService.updateFromTimeOfDay(timeOfDay, gameDTO,
//                GameDTO::setTimeOfDay,
//                null,
//                BuildingDTO::getEnergyProduction,
//                (currentValue, dayTime) -> currentValue * timeOfDay.getGenerationFactor(),
//                BuildingDTO::setEnergyProduction);
//    }
//
//    private void updateHouseholdConsumption(TimeOfDay timeOfDay, GameDTO gameDTO) {
//        helperService.updateFromTimeOfDay(timeOfDay, gameDTO,
//                GameDTO::setTimeOfDay,
//                BuildingDTO -> BuildingDTO.getHouseHolds() > 0,
//                BuildingDTO::getEnergyConsumption,
//                (currentValue, building) -> currentValue * timeOfDay.getHousingConsumptionFactor(),
//                BuildingDTO::setEnergyConsumption);
//    }
//
//    private void updateIndustrialConsumption(TimeOfDay timeOfDay, GameDTO gameDTO) {
//        helperService.updateFromTimeOfDay(timeOfDay, gameDTO,
//                GameDTO::setTimeOfDay,
//                BuildingDTO -> BuildingDTO.getGoldIncome() > 0,
//                BuildingDTO::getEnergyConsumption,
//                (currentValue, dayTime) -> currentValue * dayTime.getIndustrialConsumptionFactor(),
//                BuildingDTO::setEnergyConsumption);
//    }


    public void updateFromWeatherType(WeatherType weatherType, GameDTO gameDTO) {
        helperService.updateFromWeatherType(weatherType, gameDTO,
                GameDTO::setWeatherType,
                null,
                BuildingDTO::getEnergyProduction,
                (currentValue, weather) -> currentValue * weather.getGenerationFactor(),
                BuildingDTO::setEnergyProduction);
    }
}
