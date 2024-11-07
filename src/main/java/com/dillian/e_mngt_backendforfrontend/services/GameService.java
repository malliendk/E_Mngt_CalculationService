package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class GameService {

    private GameDTO gameDto;

    private final GameDtoBuilderService gameDTOBuilderService;
    private final BuildingStatsUpdateService buildingStatsUpdateService;
    private final BuildingUpdateService buildingUpdateService;


    public GameService(GameDtoBuilderService gameDTOBuilderService, BuildingStatsUpdateService buildingStatsUpdateService, BuildingUpdateService buildingUpdateService) {
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.buildingStatsUpdateService = buildingStatsUpdateService;
        this.buildingUpdateService = buildingUpdateService;
    }

    public GameDTO startGame(GameDTO gameDTO) {
        GameDTO newDTO = gameDTOBuilderService.buildDTO(gameDTO);
        log.info("gameDTO in service before building basic DTO: {}", newDTO);
        this.gameDto = newDTO;
        return newDTO;
    }

}

