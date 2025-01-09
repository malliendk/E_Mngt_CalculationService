package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationHelperServiceTest {

    private final CalculationHelperService testable = new CalculationHelperService();

    @Test
    void sumBuildingProperty_Double_inputList_returnDouble() {
        BuildingDTO building1 = BuildingDTO.builder()
                .solarPanelCapacity(100)
                .build();
        BuildingDTO building2 = BuildingDTO.builder()
                .solarPanelCapacity(100)
                .build();
        BuildingDTO building3 = BuildingDTO.builder()
                .solarPanelCapacity(100)
                .build();
        List<BuildingDTO> buildings = List.of(building1, building2, building3);

        double totalCapacity = testable.sumBuildingPropertyToDouble(BuildingDTO::getSolarPanelCapacity, buildings);

        assertThat(totalCapacity).isEqualTo(300);
        assertThat(totalCapacity).isNotEqualTo(0);
    }

    @Test
    void sumBuildingProperty_sumSolarPanelAmountDouble() {
        BuildingDTO buildingDTO = BuildingDTO.builder()
                .solarPanelAmount(1)
                .build();
        BuildingDTO buildingDTO2 = BuildingDTO.builder()
                .solarPanelAmount(1)
                .build();
        List<BuildingDTO> listOfBuildings = List.of(buildingDTO, buildingDTO2);

        double summedTotal = testable.sumBuildingPropertyToDouble(BuildingDTO::getSolarPanelAmount, listOfBuildings);

        assertThat(summedTotal).isEqualTo(2);
    }

}