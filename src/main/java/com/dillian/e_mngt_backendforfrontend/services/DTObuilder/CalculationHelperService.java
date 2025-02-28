package com.dillian.e_mngt_backendforfrontend.services.DTObuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

@Service
@Slf4j
public class CalculationHelperService {

    public static int sumBuildingProperty(ToIntFunction<BuildingDTO> getter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings.stream()
                .mapToInt(getter)
                .sum();
    }

    public static double updateByDayOrWeather(List<BuildingDTO> buildings, ToDoubleFunction<BuildingDTO> getter, double dayOrWeatherFactor) {
        return buildings.stream()
                .mapToDouble(building -> getter.applyAsDouble(building) * dayOrWeatherFactor)
                .sum();
    }

    public static void mapSolarProduction(BuildingDTO building, ToIntFunction<SolarPanelSetDTO> solarPanelGetter,
                                          BiConsumer<BuildingDTO, Integer> setterMethod) {
        int incomePerBuilding = solarPanelGetter.applyAsInt(building.getSolarPanelSet()) * building.getSolarPanelAmount();
        setterMethod.accept(building, incomePerBuilding);
    }
}
