package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.GameDTOBuilderService;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private GameDTO gameDto;

    private final GameDTOBuilderService gameDTOBuilderService;
    private final DayTimeSchedulerService dayTimeSchedulerService;


    public GameService(GameDTOBuilderService gameDTOBuilderService, DayTimeSchedulerService dayTimeSchedulerService) {
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.dayTimeSchedulerService = dayTimeSchedulerService;
    }

    public GameDTO updateDTO(GameDTO gameDTO) {
        return gameDTOBuilderService.buildBasicDTO(gameDTO);
    }

    public void startGame(GameDTO gameDTO) {
        gameDTOBuilderService.buildBasicDTO(gameDTO);
    }
}

