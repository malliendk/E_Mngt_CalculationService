package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DTOStatsCalculationService {

    private final StatsCalculationHelperService helperService;

    public void mapSolarProduction(List<BuildingDTO> buildings) {
        buildings.forEach(building -> {
            helperService.mapSolarProduction(SolarPanelSetDTO::getEnergyProduction, BuildingDTO::getEnergyProduction,
                    BuildingDTO::setEnergyConsumption, building);
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

    public void updateFromTimeOfDay(TimeOfDay timeOfDay, GameDTO gameDTO) {
        helperService.updateFromDayWeather(timeOfDay, gameDTO,
                BuildingDTO -> BuildingDTO.getHouseHolds() > 0,
                BuildingDTO::getEnergyConsumption,
                (currentValue, dayTime) -> currentValue * dayTime.getHousingConsumptionFactor(),
                BuildingDTO::setEnergyConsumption);
        helperService.updateFromDayWeather(timeOfDay, gameDTO,
                BuildingDTO -> BuildingDTO.getGoldIncome() > 0,
                BuildingDTO::getEnergyConsumption,
                (currentValue, dayTime) -> currentValue * dayTime.getIndustrialConsumptionFactor(),
                BuildingDTO::setEnergyConsumption);
    }

    public void updateFromWeatherType(WeatherType weatherType, GameDTO gameDTO) {
        gameDTO.getBuildings()
                .stream()
                .filter(building -> building.getGoldIncome() > 0 || building.getResearchIncome() > 0)
                .forEach(building -> {
                    double updatedValue = weatherType.getGenerationFactor() * building.getEnergyProduction();
                    building.setEnergyProduction(updatedValue);
                });
    }
}
