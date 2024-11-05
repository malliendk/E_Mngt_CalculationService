package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.BuildingStatsUpdateService;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.GameDTOBuilderService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class GameService {

    private GameDTO gameDto;

    private final GameDTOBuilderService gameDTOBuilderService;
    private final BuildingStatsUpdateService buildingStatsUpdateService;
    private final SolarPanelCalculationService solarPanelCalculationService;


    public GameService(GameDTOBuilderService gameDTOBuilderService, BuildingStatsUpdateService buildingStatsUpdateService, SolarPanelCalculationService solarPanelCalculationService) {
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.buildingStatsUpdateService = buildingStatsUpdateService;
        this.solarPanelCalculationService = solarPanelCalculationService;
    }

    public void startGame(GameDTO gameDTO) {
        log.info("gameDTO in service before building basic DTO: {}", gameDTO.toString());
        gameDTO = gameDTOBuilderService.buildBasicDTO(gameDTO);
        this.gameDto = gameDTO;
    }

    public void updateDTOFromTimeOfDay(TimeOfDay timeOfDay) {
        solarPanelCalculationService.updateSolarPanelProduction(timeOfDay, this.gameDto);

    }

    public void updateDTOFromWeatherType(WeatherType weatherType) {
        this.gameDto = buildingStatsUpdateService.updateGameByWeatherType(weatherType, this.getGameDto());
    }

    public void updateDTOIncome(GameDTO gameDTO) {
        this.gameDto = buildingStatsUpdateService.addIncome(gameDTO);
    }

}

