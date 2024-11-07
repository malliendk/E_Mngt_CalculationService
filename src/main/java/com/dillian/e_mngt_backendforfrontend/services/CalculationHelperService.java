package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.*;

@Service
public class CalculationHelperService {

    public double sumDoubleProperty(List<BuildingDTO> buildings, ToDoubleFunction<BuildingDTO> propertyFunction) {
        return buildings
                .stream()
                .mapToDouble(propertyFunction)
                .sum();
    }

    public int sumIntProperty(ToIntFunction<BuildingDTO> propertyFunction, List<BuildingDTO> buildings) {
        return buildings
                .stream()
                .mapToInt(propertyFunction)
                .sum();
    }

    public double sumBuildingProperty(ToDoubleFunction<BuildingDTO> productionGetter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings
                .stream()
                .mapToDouble(productionGetter)
                .sum();
    }

    public double mapSolarProduction(ToDoubleFunction<SolarPanelSetDTO> getSolarPanelProduction, BuildingDTO buildingDTO) {
        return buildingDTO.getSolarPanelSets()
                .stream()
                .mapToDouble(getSolarPanelProduction)
                .sum();
    }

    public BuildingDTO updateBuildingProperty(Function<BuildingDTO, Double> propertyGetter, double factor, BuildingDTO buildingDTO,
                                              BiFunction<BuildingDTO.BuildingDTOBuilder, Double, BuildingDTO.BuildingDTOBuilder> propertySetter){
        double propertyValue = propertyGetter.apply(buildingDTO);
        double updateValue = propertyValue * factor;
        return propertySetter.apply(buildingDTO.toBuilder(), updateValue).build();
    }

    public SolarPanelSetDTO updateSolarPanelProduction(FactorProvider factorProvider, SolarPanelSetDTO solarPanelSetDTO) {
        double updatedProduction = solarPanelSetDTO.getEnergyProduction() * factorProvider.getGenerationFactor();
        return solarPanelSetDTO.toBuilder()
                .energyProduction(updatedProduction)
                .build();
    }
}
