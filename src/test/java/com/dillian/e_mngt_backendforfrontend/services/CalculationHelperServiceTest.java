package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.services.DTObuilder.CalculationHelperService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationHelperServiceTest {

    @Test
    void updateByDayOrWeather() {
        final BuildingDTO building = getBuildingDTO();
        final List<BuildingDTO> buildings = List.of(building);

        TimeOfDay morning = TimeOfDay.MORNING;

        double result = CalculationHelperService.updateByDayOrWeather(buildings, BuildingDTO::getEnergyProduction,
                morning.getGenerationFactor());

        assertThat(result).isEqualTo(75);
    }

    @Test
    void mapSolarProduction_enegryProduction_shouldMapSummedValueToBuildingDTO() {
        final BuildingDTO building = getBuildingDTO();

        CalculationHelperService.mapSolarProduction(building,
                SolarPanelSetDTO::getEnergyProduction, BuildingDTO::setEnergyProduction);
        CalculationHelperService.mapSolarProduction(building,
                SolarPanelSetDTO::getResearchIncome, BuildingDTO::setResearchIncome);
        CalculationHelperService.mapSolarProduction(building,
                SolarPanelSetDTO::getGoldIncome, BuildingDTO::setGoldIncome);
        CalculationHelperService.mapSolarProduction(building,
                SolarPanelSetDTO::getEnvironmentIncome, BuildingDTO::setEnvironmentalIncome);
        assertThat(building.getEnergyProduction()).isEqualTo(100);
        assertThat(building.getResearchIncome()).isEqualTo(10);
        assertThat(building.getGoldIncome()).isEqualTo(10);
        assertThat(building.getEnvironmentalIncome()).isEqualTo(10);
    }

    private static BuildingDTO getBuildingDTO() {
        final SolarPanelSetDTO solarPanelSet = new SolarPanelSetDTO();
        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "",
                1,
                1,
                1,
                100,
                10,
                solarPanelSet,
                1,
                1);
        building.setGoldIncome(1);
        building.setResearchIncome(1);
        building.setEnvironmentalIncome(1);
        building.setEnergyProduction(10);
        return building;
    }
}