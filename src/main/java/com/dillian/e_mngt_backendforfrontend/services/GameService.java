package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.GameDTOMapper;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import com.dillian.e_mngt_backendforfrontend.services.calculations.DayWeatherService;
import com.dillian.e_mngt_backendforfrontend.services.calculations.IncomeLossCalculator;
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
    private final IncomeLossCalculator incomeLossCalculator;;
    private ExtendedGameDTO extendedGameDTO;
    private DayWeatherUpdateDTO dayWeatherUpdateDTO;
    private IncomeAddDTO incomeAddDTO;

    public GameService(final DayWeatherService dayWeatherService, final GameDTOBuilderService GameDTOBuilderService, final GameDTOMapper gameDTOMapper, final IncomeLossCalculator incomeLossCalculator) {
        this.dayWeatherService = dayWeatherService;
        this.GameDTOBuilderService = GameDTOBuilderService;
        this.gameDTOMapper = gameDTOMapper;
        this.incomeLossCalculator = incomeLossCalculator;
    }

    public void buildGameDTO(InitiateDTO initiateDTO) {
        ExtendedGameDTO updatedExtendedGameDTO = GameDTOBuilderService.buildGameDTO(initiateDTO);
        this.extendedGameDTO = updatedExtendedGameDTO;
        gameDTOMapper.toMinimizedGameDTO(updatedExtendedGameDTO);
        if (this.incomeAddDTO != null) {
            updateIncomeDTO();
        }
        if (this.dayWeatherUpdateDTO != null) {
            updateDayWeatherDTO();
        }
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

    public void addIncome(ExtendedGameDTO extendedGameDTO) {
        IncomeAddDTO incomeDTO = new IncomeAddDTO();
        incomeDTO.setNewFunds(extendedGameDTO.getFunds() + extendedGameDTO.getGoldIncome());
        incomeDTO.setNewPopularity(extendedGameDTO.getPopularity() + extendedGameDTO.getPopularityIncome());
        incomeDTO.setNewResearch(extendedGameDTO.getResearch() + extendedGameDTO.getResearchIncome());
        this.incomeAddDTO = incomeDTO;
        updateGameDTOByIncome(incomeDTO.getNewFunds(), incomeDTO.getNewPopularity(), incomeDTO.getNewResearch());
        log.info("updated gameDTO: {}", extendedGameDTO);
    }

    public void subtractPopularityIncome(ExtendedGameDTO extendedGameDTO) {
        this.extendedGameDTO = incomeLossCalculator.calculateLossByDistrictStressLevel(extendedGameDTO);
        log.info("subtracted gold and popularity income: {} {}", extendedGameDTO.getGoldIncome(), extendedGameDTO.getPopularityIncome());
    }

    private void updateGameDTOWithDayWeather(String newTimeOfDay, String newWeatherType, List<District> updatedDistricts) {
        this.extendedGameDTO.setTimeOfDay(newTimeOfDay);
        this.extendedGameDTO.setWeatherType(newWeatherType);
        this.extendedGameDTO.setDistricts(updatedDistricts);
    }

    private void updateGameDTOByIncome(double newFunds, double newPopularity, double newResearch) {
        this.extendedGameDTO.setFunds(newFunds);
        this.extendedGameDTO.setPopularity(newPopularity);
        this.extendedGameDTO.setResearch(newResearch);
    }

    private void updateIncomeDTO() {
        incomeAddDTO.setNewFunds(this.extendedGameDTO.getFunds());
        incomeAddDTO.setNewPopularity(this.extendedGameDTO.getPopularity());
        incomeAddDTO.setNewResearch(this.extendedGameDTO.getResearch());
    }

    private void updateDayWeatherDTO() {
        dayWeatherUpdateDTO.setTimeOfDay(extendedGameDTO.getTimeOfDay());
        dayWeatherUpdateDTO.setWeatherType(extendedGameDTO.getWeatherType());
        dayWeatherUpdateDTO.setDistricts(extendedGameDTO.getDistricts());
    }
}

