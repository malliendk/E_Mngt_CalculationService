package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

@Service
@Slf4j
public class CalculationHelperService {

    public static double sumBuildingPropertyToDouble(ToDoubleFunction<BuildingDTO> productionGetter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings.stream()
                .mapToDouble(productionGetter)
                .sum();
    }

    public static int sumBuildingPropertyToInt(ToIntFunction<BuildingDTO> productionGetter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings.stream()
                .mapToInt(productionGetter)
                .sum();
    }

    public static double updateByDayOrWeather(List<BuildingDTO> buildings, ToDoubleFunction<BuildingDTO> getter, double dayOrWeatherFactor) {
        return buildings.stream()
                .mapToDouble(building -> getter.applyAsDouble(building) * dayOrWeatherFactor)
                .sum();
    }

    public static void mapSolarProduction(List<BuildingDTO> buildings, ToDoubleFunction<SolarPanelSetDTO> solarPanelGetter,
                                   BiConsumer<BuildingDTO, Double> setterMethod) {
        buildings.stream()
                .filter(building -> building.getSolarPanelSet() != null)
                .forEach(building -> {
                    double incomePerBuilding = solarPanelGetter.applyAsDouble(building.getSolarPanelSet()) * building.getSolarPanelAmount();
                    setterMethod.accept(building, incomePerBuilding);
                });
    }
}
