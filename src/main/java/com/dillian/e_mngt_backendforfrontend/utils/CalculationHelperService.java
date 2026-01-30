package com.dillian.e_mngt_backendforfrontend.utils;

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.SupervisorDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.Tile;
import com.dillian.e_mngt_backendforfrontend.utils.constants.BuildingIds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

@Service
@Slf4j
public class CalculationHelperService {

    static public double calculateGridLoad(int energyProduction, int energyConsumption, int gridCapacity, SupervisorDTO supervisor) {
        return (double) ((energyProduction - energyConsumption) / gridCapacity) * supervisor.getPerkGridEfficiency();
    }

    static public int sumPowerPlantProduction(List<BuildingDTO> buildings) {
        return buildings.stream()
                .filter(Objects::nonNull)
                .filter(buildingDTO -> buildingDTO.getId().equals(BuildingIds.COAL_PLANT) ||
                        buildingDTO.getId().equals(BuildingIds.GAS_PLANT) ||
                        buildingDTO.getId().equals(BuildingIds.HYDROGEN_PLANT) ||
                        buildingDTO.getId().equals(BuildingIds.NUCLEAR_PLANT))
                .mapToInt(BuildingDTO::getEnergyProduction)
                .sum();
    }

    public static int sumBuildingProperty(ToIntFunction<BuildingDTO> getter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings.stream()
                .filter(Objects::nonNull)
                .mapToInt(getter)
                .sum();
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
