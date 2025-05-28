package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.GameDTOMapper;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import com.dillian.e_mngt_backendforfrontend.services.calculations.DayWeatherService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Slf4j
public class GameService {

    private final GameDTOBuilderService GameDTOBuilderService;
    private final GameDTOMapper gameDTOMapper;
    private final DayWeatherService dayWeatherService;
    private ExtendedGameDTO extendedGameDTO;
    private DayWeatherUpdateDTO dayWeatherUpdateDTO;
    private IncomeAddDTO incomeAddDTO;

    public GameService(final DayWeatherService dayWeatherService, final GameDTOBuilderService GameDTOBuilderService, final GameDTOMapper gameDTOMapper) {
        this.dayWeatherService = dayWeatherService;
        this.GameDTOBuilderService = GameDTOBuilderService;
        this.gameDTOMapper = gameDTOMapper;
    }

    public void buildGameDTO(InitiateDTO initiateDTO) {
        ExtendedGameDTO updatedExtendedGameDTO = GameDTOBuilderService.buildGameDTO(initiateDTO);
        this.extendedGameDTO = updatedExtendedGameDTO;
        gameDTOMapper.toMinimizedGameDTO(updatedExtendedGameDTO);
    }

    public MinimizedGameDTO minimizeGameDTO(ExtendedGameDTO extendedGameDTO) {
        return gameDTOMapper.toMinimizedGameDTO(extendedGameDTO);
    }

    public void updateByTimeOfDay(ExtendedGameDTO extendedGameDTO) {
        this.dayWeatherUpdateDTO = dayWeatherService.updateDTOByTimeOfDay(extendedGameDTO);
        this.extendedGameDTO.setDistricts(this.dayWeatherUpdateDTO.getDistricts());
        updateGameDTOWithDayWeather(dayWeatherUpdateDTO.getTimeOfDay(), dayWeatherUpdateDTO.getWeatherType(), dayWeatherUpdateDTO.getDistricts());
        log.info("gameDTO successfully updated by time of day: {}", extendedGameDTO);
    }

    public void updateByWeatherType(ExtendedGameDTO extendedGameDTO) {
        this.dayWeatherUpdateDTO = dayWeatherService.updateDTOByWeatherType(extendedGameDTO);
        this.extendedGameDTO.setDistricts(this.dayWeatherUpdateDTO.getDistricts());
        updateGameDTOWithDayWeather(dayWeatherUpdateDTO.getTimeOfDay(), dayWeatherUpdateDTO.getWeatherType(), dayWeatherUpdateDTO.getDistricts());
        log.info("gameDTO successfully updated by weather type: {}", extendedGameDTO);
    }

    private void updateGameDTOWithDayWeather(String newTimeOfDay, String newWeatherType, List<District> updatedDistricts) {
        this.extendedGameDTO.setTimeOfDay(newTimeOfDay);
        this.extendedGameDTO.setWeatherType(newWeatherType);
        this.extendedGameDTO.setDistricts(updatedDistricts);
    }

    public void addIncome(ExtendedGameDTO extendedGameDTO) {
        IncomeAddDTO incomeDTO = new IncomeAddDTO();
        incomeDTO.setNewFunds(extendedGameDTO.getFunds() + extendedGameDTO.getGoldIncome());
        incomeDTO.setNewPopularity(extendedGameDTO.getPopularity() + extendedGameDTO.getPopularityIncome());
        incomeDTO.setNewResearch(extendedGameDTO.getResearch() + extendedGameDTO.getResearchIncome());
        this.incomeAddDTO = incomeDTO;
        updateGameDTOByIncome(incomeDTO.getNewFunds(), incomeDTO.getNewPopularity(), incomeDTO.getNewResearch());
        log.info("updated gameDTO: {}", extendedGameDTO);
    }

    public void updateGameDTOByIncome(int newFunds, int newPopularity, int newResearch) {
        this.extendedGameDTO.setFunds(newFunds);
        this.extendedGameDTO.setPopularity(newPopularity);
        this.extendedGameDTO.setResearch(newResearch);
    }
}

