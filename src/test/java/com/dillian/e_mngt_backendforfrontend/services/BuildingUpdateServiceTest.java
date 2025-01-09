//package com.dillian.e_mngt_backendforfrontend.services;
//
//import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
//import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class BuildingUpdateServiceTest {
//
//    private final BuildingUpdateService testable = new BuildingUpdateService();
//
//    @Test
//    void setSolarPanelToBuilding_buildingHasSolarPanel() {
//        SolarPanelSetDTO solarPanelSetDTO = new SolarPanelSetDTO();
//        BuildingDTO building = BuildingDTO.builder()
//                .solarPanelSet(solarPanelSetDTO)
//                .build();
//
//        BuildingDTO updatedBuilding = testable.setSolarPanelToBuilding(building);
//
//        assertThat(updatedBuilding.getSolarPanelSet()).isNotNull();
//        assertThat(updatedBuilding.getSolarPanelSet().getEnergyProduction()).isEqualTo(100);
//        assertThat(updatedBuilding.getSolarPanelSet().getResearchIncome()).isEqualTo(1);
//        assertThat(updatedBuilding.getSolarPanelSet().getGoldIncome()).isEqualTo(1);
//        assertThat(updatedBuilding.getSolarPanelSet().getEnvironmentIncome()).isEqualTo(1);
//    }
//
//    @Test
//    void mapSolarProductionToBuilding() {
//        BuildingDTO buildingDTO;
//        SolarPanelSetDTO solarPanelSetDTO = new SolarPanelSetDTO();
//        int solarPanelAmount = 100;
//        double solarProduction = 100.0;
//        double goldIncome = 1;
//        double researchIncome = 1;
//        double environmentalIncome = 1;
//        buildingDTO = BuildingDTO.builder()
//                .solarPanelSet(solarPanelSetDTO)
//                .solarPanelAmount(solarPanelAmount)
//                .researchIncome(researchIncome)
//                .goldIncome(goldIncome)
//                .energyProduction(solarProduction)
//                .environmentalIncome(environmentalIncome)
//                .build();
//
//        BuildingDTO updatedBuilding = testable.mapSolarProductionToBuilding(buildingDTO);
//
//        assertThat(updatedBuilding.getSolarPanelSet()).isNotNull();
//        assertThat(updatedBuilding.getSolarPanelSet().getEnergyProduction()).isEqualTo(100);
//        assertThat(updatedBuilding.getSolarPanelSet().getResearchIncome()).isEqualTo(1);
//        assertThat(updatedBuilding.getSolarPanelSet().getGoldIncome()).isEqualTo(1);
//        assertThat(updatedBuilding.getSolarPanelSet().getEnvironmentIncome()).isEqualTo(1);
//
//        assertThat(updatedBuilding.getEnergyProduction()).isEqualTo(10000);
//        assertThat(updatedBuilding.getResearchIncome()).isEqualTo(100);
//        assertThat(updatedBuilding.getGoldIncome()).isEqualTo(100);
//        assertThat(updatedBuilding.getEnvironmentalIncome()).isEqualTo(100);
//        assertThat(updatedBuilding.getSolarPanelAmount()).isEqualTo(100);
//    }
//
//    @Test
//    void updateEnergyProduction() {
//
//    }
//
//    @Test
//    void updateHousingEnergyConsumption() {
//    }
//
//    @Test
//    void updateIndustrialConsumption() {
//    }
//
//    @Test
//    void updateGridLoad() {
//    }
//}