package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.*;

@Service
public class StatsCalculationHelperService {

    public double sumDoubleProperty(List<BuildingDTO> buildings, ToDoubleFunction<BuildingDTO> propertyFunction) {
        return buildings
                .stream()
                .mapToDouble(propertyFunction)
                .sum();
    }

    public int sumIntProperty(List<BuildingDTO> buildings, ToIntFunction<BuildingDTO> propertyFunction) {
        return buildings
                .stream()
                .mapToInt(propertyFunction)
                .sum();
    }

    public double sumBuildingProduction(List<BuildingDTO> DTOBuildings) {
        return DTOBuildings
                .stream()
                .mapToDouble(BuildingDTO::getEnergyProduction)
                .sum();
    }

    public void mapSolarProduction(ToDoubleFunction<SolarPanelSetDTO> solarPanelGetter, ToDoubleFunction<BuildingDTO> buildingGetter,
                                   BiConsumer<BuildingDTO, Double> setterMethod, BuildingDTO buildingDTO) {
        buildingDTO.getSolarPanelSets()
                .forEach(solarPanelSetDTO -> {
                    double newValue = solarPanelGetter.applyAsDouble(solarPanelSetDTO) + buildingGetter.applyAsDouble(buildingDTO);
                    setterMethod.accept(buildingDTO, newValue);
                });
    }

    public void updateFromTimeOfDay(
            TimeOfDay timeOfDay,
            GameDTO gameDTO,
            BiConsumer<GameDTO, TimeOfDay> setTimeOfDay,
            Predicate<BuildingDTO> filterPredicate,
            Function<BuildingDTO, Double> getter,
            BiFunction<Double, TimeOfDay, Double> calculation,
            BiConsumer<BuildingDTO, Double> setUpdatedValue) {

        gameDTO.getBuildings()
                .stream()
                .filter(building -> filterPredicate == null || filterPredicate.test(building))
                .forEach(building -> {
                    setTimeOfDay.accept(gameDTO, timeOfDay);
                    Double currentValue = getter.apply(building);
                    Double updatedValue = calculation.apply(currentValue, timeOfDay);
                    setUpdatedValue.accept(building, updatedValue);
                });
    }

    public void updateFromWeatherType(
            WeatherType weatherType,
            GameDTO gameDTO,
            BiConsumer<GameDTO, WeatherType> setWeatherType,
            Predicate<BuildingDTO> filterPredicate,
            Function<BuildingDTO, Double> getter,
            BiFunction<Double, WeatherType, Double> calculation,
            BiConsumer<BuildingDTO, Double> setUpdatedValue) {

        gameDTO.getBuildings()
                .stream()
                .filter(building -> filterPredicate == null || filterPredicate.test(building))
                .forEach(building -> {
                    setWeatherType.accept(gameDTO, weatherType);
                    Double currentValue = getter.apply(building);
                    Double updatedValue = calculation.apply(currentValue, weatherType);
                    setUpdatedValue.accept(building, updatedValue);
                });
    }
}
