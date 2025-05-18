//package com.dillian.e_mngt_backendforfrontend.services;
//
//import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
//import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
//import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
//import com.dillian.e_mngt_backendforfrontend.dtos.SupervisorDTO;
//import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
//import com.dillian.e_mngt_backendforfrontend.services.calculations.DayWeatherService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.HashMap;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class ExtendedGameDTOBuilderServiceTest {
//
//    @InjectMocks
//    private GameDTOBuilderService testable;
//
//    @Mock
//    private DayWeatherService dayWeatherService;
//
//
//    @Test
//    void mapSolarProduction_oneSolarPanel_shouldGiveValuesOneAndTen() {
//        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "", 1, 1, 1,
//                1, 1, new SolarPanelSetDTO(),1, 1);
//
//        final List<BuildingDTO> buildings = List.of(building);
//
//        testable.mapSolarProduction(buildings);
//
//        assertThat(building.getSolarPanelSet()).isNotNull();
//        assertThat(building.getSolarPanelAmount()).isEqualTo(1);
//        assertThat(building.getEnergyProduction()).isEqualTo(10);
//        assertThat(building.getGoldIncome()).isEqualTo(1);
//        assertThat(building.getResearchIncome()).isEqualTo(1);
//        assertThat(building.getEnvironmentalIncome()).isEqualTo(1);
//    }
//
//    @Test
//    void mapSolarProduction_twoSolarPanels_shouldGiveValuesTwoAndTwenty() {
//        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "", 1, 1, 1,
//                100, 2, new SolarPanelSetDTO(),2, 1);
//
//        final List<BuildingDTO> buildings = List.of(building);
//
//        testable.mapSolarProduction(buildings);
//
//        assertThat(building.getSolarPanelSet()).isNotNull();
//        assertThat(building.getSolarPanelAmount()).isEqualTo(2);
//        assertThat(building.getEnergyProduction()).isEqualTo(20);
//        assertThat(building.getGoldIncome()).isEqualTo(2);
//        assertThat(building.getResearchIncome()).isEqualTo(2);
//        assertThat(building.getEnvironmentalIncome()).isEqualTo(2);
//    }
//
//
//    @Test
//    void updateStats_shouldCalculateGameDTOValues() {
//        ExtendedGameDTO extendedGameDTO = getGameDTO();
//
//        final ExtendedGameDTO result = testable.buildGameDTO(extendedGameDTO);
//
//        assertThat(result.getTotalGridLoad()).isEqualTo(1);
//        assertThat(result.getGridCapacity()).isEqualTo(1);
//        assertThat(result.getEnergyProduction()).isEqualTo(100);
//        assertThat(result.getEnergyConsumption()).isEqualTo(100);
//        assertThat(result.getSolarPanelAmount()).isEqualTo(1);
//        assertThat(result.getSolarPanelCapacity()).isEqualTo(1);
//        assertThat(result.getHouseHolds()).isEqualTo(1);
//        assertThat(result.getGoldIncome()).isEqualTo(1);
//        assertThat(result.getResearchIncome()).isEqualTo(1);
//        assertThat(result.getEnvironmentalIncome()).isEqualTo(1);
//    }
//
//    @Test
//    void updateDtoByTimeOfDay_timeOfDayMorning_shouldUpdateGameDTOEnergyProductionAndConsumption() {
//        TimeOfDay morning = TimeOfDay.MORNING;
//        when(dayWeatherService.cycleThroughTimesOfDay()).thenReturn(morning);
//
//        final ExtendedGameDTO extendedGameDTO = getGameDTO();
//        final ExtendedGameDTO result = testable.updateDTOByTimeOfDay(extendedGameDTO);
//
//        assertThat(result.getTimeOfDay().getGenerationFactor()).isEqualTo(0.75);
//        assertThat(result.getEnergyProduction()).isEqualTo(75);
//        assertThat(result.getEnergyConsumption()).isEqualTo(225);
//    }
//
//    private static ExtendedGameDTO getGameDTO() {
//        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "",
//                1,
//                1,
//                1,
//                100,
//                1,
//                new SolarPanelSetDTO(),
//                1,
//                1);
//        building.setGoldIncome(1);
//        building.setResearchIncome(1);
//        building.setEnvironmentalIncome(1);
//        building.setEnergyProduction(100);
//        final List<BuildingDTO> buildings = List.of(building);
//        return new ExtendedGameDTO(new HashMap<>(), 1L, , buildings, new SupervisorDTO());
//    }
//}