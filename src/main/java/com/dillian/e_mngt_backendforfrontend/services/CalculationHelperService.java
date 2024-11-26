package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

@Service
@Slf4j
public class CalculationHelperService {

    public double sumBuildingProperty(ToDoubleFunction<BuildingDTO> productionGetter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings
                .stream()
                .mapToDouble(productionGetter)
                .sum();
    }

    /**
     * return the aggregated production values of all solar panels on a building
     *
     * @param getSolarPanelProduction dynamic getter for getting the value of any SolarPanelSetDTO
     *                               property that returns a double
     * @param buildingDTO that carries the SolarPanelSetDTO and to which the aggregated production values of
     *                    the solar panel is mapped
     * @param solarPanelAmount multiplier for the returned value of the SolarPanelSetDTO production
     * @return the new total amount of the multiplied value

     */
    public double mapSolarProduction(ToDoubleFunction<SolarPanelSetDTO> getSolarPanelProduction, BuildingDTO buildingDTO, int solarPanelAmount) {
        return getSolarPanelProduction.applyAsDouble(buildingDTO.getSolarPanelSet()) * solarPanelAmount;
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
