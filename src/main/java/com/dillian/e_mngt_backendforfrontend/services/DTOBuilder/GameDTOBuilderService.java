package com.dillian.e_mngt_backendforfrontend.services.DTOBuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameDTOBuilderService {

    private final DTOStatsCalculationService statsCalculationService;

    public GameDTOBuilderService(DTOStatsCalculationService statsCalculationService) {
        this.statsCalculationService = statsCalculationService;
    }


    public GameDTO buildBasicDTO(GameDTO gameDTO) {
        statsCalculationService.mapSolarProduction(gameDTO.getBuildings()
                .stream()
                .filter(buildingDTO -> buildingDTO.getSolarPanelSets() != null)
                .toList());
        gameDTO = statsCalculationService.calculateBasicStats(gameDTO);
        return gameDTO;
    }
}
