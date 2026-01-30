package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.GameDTOMapper;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
import com.dillian.e_mngt_backendforfrontend.services.calculations.DayWeatherService;
import com.dillian.e_mngt_backendforfrontend.services.calculations.IncomeLossCalculator;
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
    private final IncomeLossCalculator incomeLossCalculator;;
    private final BuildingService buildingService;
    private final IncomeUpdateService incomeUpdateService;
    private ExtendedGameDTO extendedGameDTO;
    private DayWeatherUpdateDTO dayWeatherUpdateDTO;
    private IncomeDTO incomeDTO;

    public GameService(final DayWeatherService dayWeatherService, final GameDTOBuilderService GameDTOBuilderService, final GameDTOMapper gameDTOMapper, final IncomeLossCalculator incomeLossCalculator, final BuildingService buildingService, final IncomeUpdateService incomeUpdateService) {
        this.dayWeatherService = dayWeatherService;
        this.GameDTOBuilderService = GameDTOBuilderService;
        this.gameDTOMapper = gameDTOMapper;
        this.incomeLossCalculator = incomeLossCalculator;
        this.buildingService = buildingService;
        this.incomeUpdateService = incomeUpdateService;
    }

    public void buildGameDTO(InitiateDTO initiateDTO) {
        ExtendedGameDTO updatedExtendedGameDTO = GameDTOBuilderService.extendToGameDTO(initiateDTO);
        this.extendedGameDTO = updatedExtendedGameDTO;
        gameDTOMapper.toMinimizedGameDTO(updatedExtendedGameDTO);
        this.incomeDTO = incomeUpdateService.createIncomeDTO(this.extendedGameDTO);
        this.dayWeatherUpdateDTO = dayWeatherService.setProductionAndConsumption(dayWeatherUpdateDTO, extendedGameDTO);
    }

    public MinimizedGameDTO minimizeGameDTO(ExtendedGameDTO extendedGameDTO) {
        return gameDTOMapper.toMinimizedGameDTO(extendedGameDTO);
    }

    public void updateByTimeOfDay(ExtendedGameDTO extendedGameDTO) {
        this.dayWeatherUpdateDTO = dayWeatherService.updateDTOByTimeOfDay(extendedGameDTO);
        updateGameDTOWithDayWeather(dayWeatherUpdateDTO.getTimeOfDay(), dayWeatherUpdateDTO.getWeatherType());
        log.info("gameDTO successfully updated by time of day: {}", extendedGameDTO);
    }

    public void updateByWeatherType(ExtendedGameDTO extendedGameDTO) {
        this.dayWeatherUpdateDTO = dayWeatherService.updateDTOByWeatherType(extendedGameDTO);
        updateGameDTOWithDayWeather(dayWeatherUpdateDTO.getTimeOfDay(), dayWeatherUpdateDTO.getWeatherType());
        log.info("gameDTO successfully updated by weather type: {}", extendedGameDTO);
    }

    public void addIncome() {
        this.extendedGameDTO = incomeUpdateService.addIncomeToGameDTO(incomeDTO, extendedGameDTO);
        log.info("income updated: {}, {}, {}", extendedGameDTO.getFunds(), extendedGameDTO.getPopularity(), extendedGameDTO.getResearch());
    }

    public void subtractPopularityIncome(ExtendedGameDTO extendedGameDTO) {
        this.extendedGameDTO = incomeLossCalculator.calculateLossByDistrictStressLevel(extendedGameDTO);
        log.info("subtracted gold and popularity income: {} {}", extendedGameDTO.getGoldIncome(), extendedGameDTO.getPopularityIncome());
    }

    private void updateGameDTOWithDayWeather(String newTimeOfDay, String newWeatherType) {
        this.extendedGameDTO.setTimeOfDay(newTimeOfDay);
        this.extendedGameDTO.setWeatherType(newWeatherType);
        this.extendedGameDTO = buildingService.mapEnergyUpdates(this.dayWeatherUpdateDTO, this.extendedGameDTO);
    }

    private void updateIncomeDTO() {
        incomeDTO.setGoldIncome(this.extendedGameDTO.getFunds());
        incomeDTO.setPopularityIncome(this.extendedGameDTO.getPopularity());
        incomeDTO.setResearchIncome(this.extendedGameDTO.getResearch());
    }

    private void updateDayWeatherDTO() {
        dayWeatherUpdateDTO.setTimeOfDay(extendedGameDTO.getTimeOfDay());
        dayWeatherUpdateDTO.setWeatherType(extendedGameDTO.getWeatherType());

    }
}

