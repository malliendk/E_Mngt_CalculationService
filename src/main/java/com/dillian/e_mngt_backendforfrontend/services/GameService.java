package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.DTOStatsCalculationService;
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
    private final DTOStatsCalculationService dtoStatsCalculationService;


    public GameService(GameDTOBuilderService gameDTOBuilderService, DTOStatsCalculationService dtoStatsCalculationService) {
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.dtoStatsCalculationService = dtoStatsCalculationService;
    }

    public void startGame(GameDTO gameDTO) {
        log.info("gameDTO in service before building basic DTO: {}", gameDTO.toString());
        gameDTO = gameDTOBuilderService.buildBasicDTO(gameDTO);
        this.gameDto = gameDTO;
    }

    public void updateDTOFromTimeOfDay(TimeOfDay timeOfDay) {
        this.gameDto = dtoStatsCalculationService.updateGameByTimeOfDay(timeOfDay, this.getGameDto());
    }

    public void updateDTOFromWeatherType(WeatherType weatherType) {
        this.gameDto = dtoStatsCalculationService.updateGameByWeatherType(weatherType, this.getGameDto());
    }

    public void updateDTOIncome(GameDTO gameDTO) {
        this.gameDto = dtoStatsCalculationService.addIncome(gameDTO);
    }

}

