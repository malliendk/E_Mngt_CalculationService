package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
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

    public void mapSolarProduction(ToDoubleFunction<SolarPanelSetDTO> solarPanelGetter, ToDoubleFunction<BuildingDTO> buildingGetter,
                                   BiConsumer<BuildingDTO, Double> setterMethod, BuildingDTO buildingDTO) {
        buildingDTO.getSolarPanelSets()
                .forEach(solarPanelSetDTO -> {
                    double newValue = solarPanelGetter.applyAsDouble(solarPanelSetDTO) + buildingGetter.applyAsDouble(buildingDTO);
                    setterMethod.accept(buildingDTO, newValue);
                });
    }

    public void updateFromDayWeather(
            TimeOfDay timeOfDay,
            GameDTO gameDTO,
            Predicate<BuildingDTO> filterPredicate,
            Function<BuildingDTO, Double> getter,
            BiFunction<Double, TimeOfDay, Double> calculation,
            BiConsumer<BuildingDTO, Double> setter) {

        gameDTO.getBuildings()
                .stream()
                .filter(filterPredicate)
                .forEach(building -> {
                    Double currentValue = getter.apply(building);
                    Double updatedValue = calculation.apply(currentValue, timeOfDay);
                    setter.accept(building, updatedValue);
                });
    }
}
