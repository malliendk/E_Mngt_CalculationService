//package com.dillian.e_mngt_backendforfrontend.services;
//
//import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.BuildingDTO;
//import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.SolarPanelSetDTO;
//import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
//import com.dillian.e_mngt_backendforfrontend.services.utils.CalculationHelperService;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class CalculationHelperServiceTest {
//
//    @Test
//    void updateByDayOrWeather() {
//        final BuildingDTO building = getBuildingDTO();
//        final List<BuildingDTO> buildings = List.of(building);
//
//        TimeOfDay morning = TimeOfDay.MORNING;
//
//        double result = CalculationHelperService.updateByDayOrWeather(buildings, BuildingDTO::getEnergyProduction,
//                morning.getGenerationFactor());
//
//        assertThat(result).isEqualTo(75);
//    }
//
//    @Test
//    void mapSolarProduction_energyProduction_shouldMapSummedValueToBuildingDTO() {
//        final BuildingDTO building = getBuildingDTO();
//
//        CalculationHelperService.mapSolarProduction(building,
//                SolarPanelSetDTO::getEnergyProduction, BuildingDTO::setEnergyProduction);
//        CalculationHelperService.mapSolarProduction(building,
//                SolarPanelSetDTO::getResearchIncome, BuildingDTO::setResearchIncome);
//        CalculationHelperService.mapSolarProduction(building,
//                SolarPanelSetDTO::getGoldIncome, BuildingDTO::setGoldIncome);
//        CalculationHelperService.mapSolarProduction(building,
//                SolarPanelSetDTO::getEnvironmentScore, BuildingDTO::setEnvironmentalScore);
//        assertThat(building.getEnergyProduction()).isEqualTo(100);
//        assertThat(building.getResearchIncome()).isEqualTo(10);
//        assertThat(building.getGoldIncome()).isEqualTo(10);
//        assertThat(building.getEnvironmentalScore()).isEqualTo(10);
//    }
//
//    private static BuildingDTO getBuildingDTO() {
//        final SolarPanelSetDTO solarPanelSet = new SolarPanelSetDTO();
//        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "",
//                1,
//                1,
//                1,
//                solarPanelSet,
//                1);
//        building.setSolarPanelAmount(10);
//        building.setGoldIncome(1);
//        building.setResearchIncome(1);
//        building.setEnvironmentalScore(1);
//        building.setEnergyProduction(10);
//        return building;
//    }
//}