package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class GameService {

    private GameDTO gameDTO;

    private final GameDtoBuilderService gameDTOBuilderService;
    private final BuildingUpdateService buildingUpdateService;
    private final CalculationHelperService calculationHelperService;

    public GameService(CalculationHelperService calculationHelperService, GameDtoBuilderService gameDTOBuilderService, BuildingUpdateService buildingUpdateService) {
        this.calculationHelperService = calculationHelperService;
        this.gameDTOBuilderService = gameDTOBuilderService;
        this.buildingUpdateService = buildingUpdateService;
    }

    public void buildGameDTO(GameDTO gameDTO) {
        gameDTO = gameDTOBuilderService.mapSolarIncome(gameDTO);
        gameDTO = gameDTOBuilderService.updateEnergyProduction(gameDTO);
        gameDTO = gameDTOBuilderService.updateEnergyConsumption(gameDTO);
        gameDTO = gameDTOBuilderService.updateGridLoad(gameDTO);
        gameDTO = gameDTOBuilderService.updateGridCapacity(gameDTO);
        this.gameDTO = gameDTO.toBuilder().build();
    }

    public void updateDtoByTimeOfDay(TimeOfDay timeOfDay, GameDTO gameDTO) {
        gameDTO = gameDTO.toBuilder().timeOfDay(timeOfDay).build();
        this.gameDTO = gameDTOBuilderService.updateEnergyConsumption(timeOfDay, gameDTO);
        this.gameDTO = gameDTOBuilderService.updateEnergyProduction(timeOfDay, gameDTO);
    }

    public void updateDtoByWeatherType(WeatherType weatherType, GameDTO gameDTO) {
        gameDTO = gameDTO.toBuilder().weatherType(weatherType).build();
        this.gameDTO = gameDTOBuilderService.updateEnergyProduction(weatherType, gameDTO);
    }

    public void addIncomeToDTO(GameDTO gameDTO) {
        this.gameDTO = gameDTOBuilderService.addIncome(gameDTO);
    }
}

