package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class GameService {

    private GameDTO gameDTO;

    private final DayWeatherService dayWeatherService;
    private final GameDTOBuilderService gameDTOBuilderService;

    public GameService(GameDTOBuilderService gameDTOBuilderService, final DayWeatherService dayWeatherService) {
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.dayWeatherService = dayWeatherService;
    }

    public GameDTO buildGameDTO(GameDTO gameDTO) {
        //accumulated income from solar panels is mapped onto their holding buildings
        //then gameDTO value are updated by the summed values of all buildings' properties
        gameDTOBuilderService.mapSolarProduction(gameDTO.getBuildings());
        this.gameDTO = gameDTOBuilderService.updateStats(gameDTO);
        return this.gameDTO;
    }

    public void updateByTimeOfDay(GameDTO gameDTO) {
        this.gameDTO = gameDTOBuilderService.updateDTOByTimeOfDay(gameDTO);
        log.info("gameDTO successfully updated by time of day: {}", gameDTO);
    }

    public void updateByWeatherType(GameDTO gameDTO) {
        this.gameDTO = gameDTOBuilderService.updateDTOByWeatherType(gameDTO);
        log.info("gameDTO successfully updated by weather type: {}", gameDTO);
    }

    public void addIncome(GameDTO gameDTO) {
        gameDTO.setFunds(gameDTO.getFunds() + gameDTO.getGoldIncome());
        gameDTO.setPopularity(gameDTO.getPopularity() + gameDTO.getPopularityIncome());
        gameDTO.setResearch(gameDTO.getResearch() + gameDTO.getResearchIncome());
        gameDTO.setEnvironmentalScore(gameDTO.getEnvironmentalScore() + gameDTO.getEnvironmentalIncome());
        log.info("updated gameDTO: {}", gameDTO);
    }
}

