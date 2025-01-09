package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SupervisorDTO;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GameDTOBuilderServiceTest {

    private final GameDTOBuilderService testable = new GameDTOBuilderService();


    @Test
    void mapSolarProduction_oneSolarPanel_shouldGiveValuesOneAndTen() {
        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "", 1, 1, 1,
                1, 1, new SolarPanelSetDTO(),1, 1);

        final List<BuildingDTO> buildings = List.of(building);

        testable.mapSolarProduction(buildings);

        assertThat(building.getSolarPanelSet()).isNotNull();
        assertThat(building.getSolarPanelAmount()).isEqualTo(1);
        assertThat(building.getEnergyProduction()).isEqualTo(10);
        assertThat(building.getGoldIncome()).isEqualTo(1);
        assertThat(building.getResearchIncome()).isEqualTo(1);
        assertThat(building.getEnvironmentalIncome()).isEqualTo(1);
    }

    @Test
    void mapSolarProduction_twoSolarPanels_shouldGiveValuesTwoAndTwenty() {
        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "", 1, 1, 1,
                1, 2, new SolarPanelSetDTO(),2, 1);

        final List<BuildingDTO> buildings = List.of(building);

        testable.mapSolarProduction(buildings);

        assertThat(building.getSolarPanelSet()).isNotNull();
        assertThat(building.getSolarPanelAmount()).isEqualTo(2);
        assertThat(building.getEnergyProduction()).isEqualTo(20);
        assertThat(building.getGoldIncome()).isEqualTo(2);
        assertThat(building.getResearchIncome()).isEqualTo(2);
        assertThat(building.getEnvironmentalIncome()).isEqualTo(2);
    }


    @Test
    void calculateBasicStats() {
        final BuildingDTO building = new BuildingDTO(1L, "", "",0, "", 1, 1, 1,
                1, 1, new SolarPanelSetDTO(),1, 1);

        final List<BuildingDTO> buildings = List.of(building);
        GameDTO gameDTO = new GameDTO(new HashMap<>(), 1L, buildings, new SupervisorDTO());

        final GameDTO result = testable.calculateBasicStats(gameDTO);

        assertThat(result.getTotalGridLoad()).isEqualTo(1);
        assertThat(result.getEnergyProduction()).isEqualTo(10);
        assertThat(result.getGoldIncome()).isEqualTo(1);
        assertThat(result.getResearchIncome()).isEqualTo(1);
        assertThat(result.getEnvironmentalIncome()).isEqualTo(1);
        assertThat(result.getGridCapacity()).isEqualTo(1);
        assertThat(result.getHouseHolds()).isEqualTo(1);
    }
}