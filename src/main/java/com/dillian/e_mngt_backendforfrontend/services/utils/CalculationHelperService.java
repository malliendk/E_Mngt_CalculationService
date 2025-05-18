package com.dillian.e_mngt_backendforfrontend.services.utils;

import com.dillian.e_mngt_backendforfrontend.services.utils.constants.BuildingIds;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

@Service
@Slf4j
public class CalculationHelperService {

    static public double calculateGridLoad(int energyProduction, int energyConsumption, int gridCapacity) {
        return (double)(energyProduction - energyConsumption) / gridCapacity;
    }

    static public int sumPowerPlantProduction(List<BuildingDTO> buildings) {
        int totalPowerPlantProduction = (int)buildings.stream()
                .filter(buildingDTO -> buildingDTO.getId().equals(BuildingIds.COAL_PLANT) ||
                        buildingDTO.getId().equals(BuildingIds.GAS_PLANT) ||
                        buildingDTO.getId().equals(BuildingIds.HYDROGEN_PLANT) ||
                        buildingDTO.getId().equals(BuildingIds.NUCLEAR_PLANT))
                .mapToDouble(BuildingDTO::getEnergyProduction)
                .sum();
        log.info("Successfully calculated power plant production of {} buildings: {}",
                buildings.size(), totalPowerPlantProduction);
        return totalPowerPlantProduction;
    }

    public static int sumBuildingProperty(ToIntFunction<BuildingDTO> getter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings.stream()
                .filter(Objects::nonNull)
                .mapToInt(getter)
                .sum();
    }

    public static double updateByDayOrWeather(List<BuildingDTO> buildings, ToDoubleFunction<BuildingDTO> getter, double dayOrWeatherFactor) {
        return buildings.stream()
                .filter(Objects::nonNull)
                .mapToDouble(building -> getter.applyAsDouble(building) * dayOrWeatherFactor)
                .sum();
    }

    public static void mapSolarProduction(BuildingDTO building, ToIntFunction<SolarPanelSetDTO> solarPanelGetter,
                                          BiConsumer<BuildingDTO, Integer> setterMethod) {
        int incomePerBuilding = solarPanelGetter.applyAsInt(building.getSolarPanelSet()) * building.getSolarPanelAmount();
        setterMethod.accept(building, incomePerBuilding);
    }

    public static List<BuildingDTO> getBuildingsFromTiles(District district) {
        return district.getTiles().stream()
                .map(Tile::getBuilding)
                .toList();
    }

    public static void removeBuildingsFromDistrict(List<District> districts) {
        for (District district : districts) {
            district.getTiles().forEach(tile -> tile.setBuilding(null));
        }
    }
}
