package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

@Service
@Slf4j
public class CalculationHelperService {

    public double sumBuildingPropertyToDouble(ToDoubleFunction<BuildingDTO> productionGetter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings
                .stream()
                .mapToDouble(productionGetter)
                .sum();
    }

    public int sumBuildingPropertyToInt(ToIntFunction<BuildingDTO> productionGetter, List<BuildingDTO> DTOBuildings) {
        return DTOBuildings
                .stream()
                .mapToInt(productionGetter)
                .sum();
    }
}
