package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

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
public class DTOStatsCalculationService {

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

    public GameDTO calculateBasicStats(GameDTO gameDTO) {
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

    public GameDTO addIncome(GameDTO gameDTO) {
        gameDTO.setFunds(gameDTO.getFunds() + gameDTO.getGoldIncome());
        gameDTO.setPopularity(gameDTO.getPopularity() + gameDTO.getPopularityIncome());
        gameDTO.setResearch(gameDTO.getResearch() + gameDTO.getResearchIncome());
        gameDTO.setEnvironmentalScore(gameDTO.getEnvironmentalScore() + gameDTO.getEnvironmentalIncome());
        log.info("updated gameDTO: {}", gameDTO);
        return gameDTO;
    }

    public GameDTO updateGameByTimeOfDay(TimeOfDay timeOfDay, GameDTO gameDTO) {
        gameDTO.setTimeOfDay(timeOfDay);
        gameDTO.getBuildings()
                .forEach(building -> {
                    double newProduction = building.getEnergyProduction() * timeOfDay.getGenerationFactor();
                    building.setEnergyProduction(newProduction);
                });
        gameDTO.getBuildings()
                .stream()
                .filter(building -> building.getHouseHolds() > 0)
                .forEach(building -> {
                    double newConsumption = building.getEnergyConsumption() * timeOfDay.getHousingConsumptionFactor();
                    building.setEnergyConsumption(newConsumption);
                });
        gameDTO.getBuildings()
                .stream()
                .filter(building -> building.getGoldIncome() > 0)
                .forEach(building -> {
                    double newConsumption = building.getEnergyConsumption() * timeOfDay.getIndustrialConsumptionFactor();
                    building.setEnergyConsumption(newConsumption);
                });
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
