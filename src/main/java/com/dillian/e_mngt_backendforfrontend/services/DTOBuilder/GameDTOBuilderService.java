package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Getter
@Slf4j
public class GameDTOBuilderService {

    private GameDTO gameDTO;

    private final DTOStatsCalculationService statsCalculationService;

    public GameDTOBuilderService(DTOStatsCalculationService statsCalculationService) {
        this.statsCalculationService = statsCalculationService;
    }


    public GameDTO buildBasicDTO(GameDTO gameDTO) {
        statsCalculationService.mapSolarProduction(gameDTO.getBuildings()
                .stream()
                .filter(buildingDTO -> !buildingDTO.getSolarPanelSets().isEmpty())
                .toList());
        this.gameDTO = statsCalculationService.calculateBasicStats(gameDTO);
        return this.gameDTO;
    }

    public void addIncome(GameDTO gameDTO) {
        gameDTO.setFunds(gameDTO.getFunds() + gameDTO.getGoldIncome());
        gameDTO.setPopularity(gameDTO.getPopularity() + gameDTO.getPopularity());
        gameDTO.setResearch(gameDTO.getResearch() + gameDTO.getResearch());
        gameDTO.setEnvironmentalScore(gameDTO.getEnvironmentalScore() + gameDTO.getEnvironmentalIncome());
        this.gameDTO = gameDTO;
        log.info("updated gameDTO: {}", this.gameDTO);
    }
}
