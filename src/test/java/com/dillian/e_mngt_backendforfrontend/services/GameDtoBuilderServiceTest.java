package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameDtoBuilderServiceTest {

    @InjectMocks
    private GameDtoBuilderService testable;

    @Mock
    private CalculationHelperService calculationHelperService;

    @Mock
    private BuildingUpdateService buildingUpdateService;

    @Mock
    private FactorProvider factorProvider;

    @Test
    void setSolarPanels() {
        BuildingDTO buildingDTO = BuildingDTO.builder()
                .solarPanelAmount(1)
                .build();
        List<BuildingDTO> buildingDTOList = List.of(buildingDTO);
        GameDTO gameDTO = GameDTO.builder()
                .buildings(buildingDTOList)
                .build();
        when(buildingUpdateService.setSolarPanelToBuilding(buildingDTO)).thenReturn(buildingDTO);

        GameDTO updatedGameDTO = testable.setSolarPanels(gameDTO);

        assertThat(updatedGameDTO.getBuildings()).size().isGreaterThan(0);
        assertThat(updatedGameDTO.getBuildings()).containsExactly(buildingDTO);
    }

    @Test
    void mapSolarIncome() {
    }

    @Test
    void updateSolarPanelAmount() {
        BuildingDTO buildingDTO1 = BuildingDTO.builder()
                .solarPanelAmount(1)
                .solarPanelSet(new SolarPanelSetDTO())
                .build();
        BuildingDTO buildingDTO2 = BuildingDTO.builder()
                .solarPanelSet(new SolarPanelSetDTO())
                .solarPanelAmount(1)
                .build();
        BuildingDTO buildingDTO3 = BuildingDTO.builder()
                .solarPanelSet(new SolarPanelSetDTO())
                .solarPanelAmount(1)
                .build();
        List<BuildingDTO> buildingDTOList = List.of(buildingDTO1, buildingDTO2, buildingDTO3);
        GameDTO gameDTO = GameDTO.builder()
                .buildings(buildingDTOList)
                .build();

        GameDTO updatedGameDTO = testable.updateSolarPanelAmount(gameDTO);

        assertThat(updatedGameDTO.getBuildings()).size().isEqualTo(3);
        assertThat(updatedGameDTO.getBuildings().getFirst().getSolarPanelAmount()).isEqualTo(1);
        assertThat(updatedGameDTO.getSolarPanelTotalAmount()).isEqualTo(3);
    }

    @Test
    void updateSolarPanelCapacity() {
        BuildingDTO buildingDTO1 = BuildingDTO.builder()
                .solarPanelCapacity(1)
                .solarPanelSet(new SolarPanelSetDTO())
                .build();
        BuildingDTO buildingDTO2 = BuildingDTO.builder()
                .solarPanelSet(new SolarPanelSetDTO())
                .solarPanelCapacity(1)
                .build();
        BuildingDTO buildingDTO3 = BuildingDTO.builder()
                .solarPanelSet(new SolarPanelSetDTO())
                .solarPanelCapacity(1)
                .build();
        List<BuildingDTO> buildingDTOList = List.of(buildingDTO1, buildingDTO2, buildingDTO3);
        GameDTO gameDTO = GameDTO.builder()
                .buildings(buildingDTOList)
                .build();

        GameDTO updatedGameDTO = testable.updateSolarPanelCapacity(gameDTO);

        assertThat(updatedGameDTO.getSolarPanelCapacity()).isEqualTo(3);
    }


    @Test
    void updateEnergyProductionByDayWeather() {

        BuildingDTO buildingDTO = BuildingDTO.builder()
                .energyProduction(10)
                .build();
        BuildingDTO buildingDTO2 = BuildingDTO.builder()
                .energyProduction(10)
                .build();
        BuildingDTO buildingDTO3 = BuildingDTO.builder()
                .energyProduction(10)
                .build();
        List<BuildingDTO> buildingDTOList = List.of(buildingDTO, buildingDTO2, buildingDTO3);
        GameDTO gameDTO = GameDTO.builder()
                .buildings(buildingDTOList)
                .build();
        when(buildingUpdateService.updateEnergyProduction(any(FactorProvider.class), any(BuildingDTO.class)))
                .thenAnswer(invocation -> {
                    FactorProvider factor = invocation.getArgument(0);
                    BuildingDTO building = invocation.getArgument(1);
                    return building.toBuilder()
                            .energyProduction(building.getEnergyProduction() * factor.getGenerationFactor())
                            .build();
                });
        GameDTO updatedGameDTO = testable.updateEnergyProductionByDayWeather(factorProvider, gameDTO);

        assertThat(updatedGameDTO.getBuildings()).size().isEqualTo(3);
        assertThat(updatedGameDTO.getBuildings().containsAll(buildingDTOList)).isTrue();
        assertThat(updatedGameDTO.getEnergyProduction()).isEqualTo(30);

    }

    @Test
    void updateEnergyConsumptionByDayWeather() {
    }

    @Test
    void testUpdateEnergyProductionByDayWeather() {

    }

    @Test
    void testUpdateEnergyConsumptionByDayWeather() {
    }

    @Test
    void updateGridLoad() {
    }

    @Test
    void updateGridCapacity() {
    }

    @Test
    void addIncome() {
    }
}