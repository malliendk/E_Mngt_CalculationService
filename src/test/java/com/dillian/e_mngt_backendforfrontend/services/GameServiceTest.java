package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SupervisorDTO;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringJUnitConfig

class GameServiceTest {

    @InjectMocks
    private GameService testable;

    @Mock
    private DayWeatherService dayWeatherService;

    @Mock
    private GameDTOBuilderService gameDTOBuilderService;


    @Mock
    private WeatherType weatherTypeMock;

    private GameDTO gameDTOStub;

    @BeforeEach
    void setUp() {
        final BuildingDTO housingBuilding = new BuildingDTO(1L, "", "",0, "",
                0,
                1,
                0,
                100,
                0,
                new SolarPanelSetDTO(),
                0,
                0);
        housingBuilding.setEnergyProduction(100);
        final BuildingDTO industrialBuilding = new BuildingDTO(1L, "", "",0, "",
                0,
                0,
                0,
                100,
                0,
                new SolarPanelSetDTO(),
                0,
                0);
        industrialBuilding.setGoldIncome(1);
        final List<BuildingDTO> buildings = List.of(housingBuilding, industrialBuilding);
        gameDTOStub = new GameDTO(new HashMap<>(), 1L, buildings, new SupervisorDTO());
    }

    @Test
    void buildGameDTO_shoudReturnGameDTO() {
        when(gameDTOBuilderService.updateStats(gameDTOStub)).thenReturn(gameDTOStub);

        final GameDTO result = testable.buildGameDTO(gameDTOStub);

        assertThat(result).isEqualTo(gameDTOStub);
    }



}