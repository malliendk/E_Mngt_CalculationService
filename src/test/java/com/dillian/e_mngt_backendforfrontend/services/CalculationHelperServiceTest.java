package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationHelperServiceTest {

    @Test
    void updateByDayOrWeather() {
        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "",
                1,
                1,
                1,
                100,
                1,
                new SolarPanelSetDTO(),
                1,
                1);
        building.setGoldIncome(1);
        building.setResearchIncome(1);
        building.setEnvironmentalIncome(1);
        building.setEnergyProduction(100);
        final List<BuildingDTO> buildings = List.of(building);

        TimeOfDay morning = TimeOfDay.MORNING;

        double result = CalculationHelperService.updateByDayOrWeather(buildings, BuildingDTO::getEnergyProduction,
                morning.getGenerationFactor());

        assertThat(result).isEqualTo(75);
    }

}