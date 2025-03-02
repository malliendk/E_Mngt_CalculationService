package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.GameDTOMapper;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.DTObuilder.DayWeatherService;
import com.dillian.e_mngt_backendforfrontend.services.DTObuilder.GameDTOBuilderService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class GameService {

    private final GameDTOBuilderService GameDTOBuilderService;
    private final GameDTOMapper gameDTOMapper;
    private final DayWeatherService dayWeatherService;

    private ExtendedGameDTO extendedGameDTO;

    public GameService(final DayWeatherService dayWeatherService, final GameDTOBuilderService GameDTOBuilderService, final GameDTOMapper gameDTOMapper) {
        this.dayWeatherService = dayWeatherService;
        this.GameDTOBuilderService = GameDTOBuilderService;
        this.gameDTOMapper = gameDTOMapper;
    }

    public MinimizedGameDTO buildGameDTO(InitiateDTO initiateDTO) {
        ExtendedGameDTO updatedExtendedGameDTO = GameDTOBuilderService.buildGameDTO(initiateDTO);
        this.extendedGameDTO = updatedExtendedGameDTO;
        return gameDTOMapper.toMinimizedGameDTO(updatedExtendedGameDTO);
    }

    public MinimizedGameDTO minimizeGameDTO(ExtendedGameDTO extendedGameDTO) {
        return gameDTOMapper.toMinimizedGameDTO(extendedGameDTO);
    }

    public void updateByTimeOfDay(ExtendedGameDTO extendedGameDTO) {
        this.extendedGameDTO = dayWeatherService.updateDTOByTimeOfDay(extendedGameDTO);
        log.info("gameDTO successfully updated by time of day: {}", extendedGameDTO);
    }

    public void updateByWeatherType(ExtendedGameDTO extendedGameDTO) {
        this.extendedGameDTO = dayWeatherService.updateDTOByWeatherType(extendedGameDTO);
        log.info("gameDTO successfully updated by weather type: {}", extendedGameDTO);
    }

    public void addIncome(ExtendedGameDTO extendedGameDTO) {
        extendedGameDTO.setFunds(extendedGameDTO.getFunds() + extendedGameDTO.getGoldIncome());
        extendedGameDTO.setPopularity(extendedGameDTO.getPopularity() + extendedGameDTO.getPopularityIncome());
        extendedGameDTO.setResearch(extendedGameDTO.getResearch() + extendedGameDTO.getResearchIncome());
        log.info("updated gameDTO: {}", extendedGameDTO);
    }
}

