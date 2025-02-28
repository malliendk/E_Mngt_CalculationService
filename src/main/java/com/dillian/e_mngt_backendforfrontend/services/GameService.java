package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.*;
import com.dillian.e_mngt_backendforfrontend.services.DTObuilder.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Slf4j
public class GameService {

    private final GameDTOBuilderService GameDTOBuilderService;
    private ExtendedGameDTO extendedGameDTO;

    private final DayWeatherService dayWeatherService;

    public GameService(final DayWeatherService dayWeatherService, final GameDTOBuilderService GameDTOBuilderService) {
        this.dayWeatherService = dayWeatherService;
        this.GameDTOBuilderService = GameDTOBuilderService;
    }

    public ExtendedGameDTO buildGameDTO(InitiateDTO initiateDTO) {
        ExtendedGameDTO updatedExtendedGameDTO = GameDTOBuilderService.buildGameDTO(initiateDTO);
        this.extendedGameDTO = updatedExtendedGameDTO;
        return updatedExtendedGameDTO;
    }

    public MinimizedGameDTO minimizeGameDTO(ExtendedGameDTO extendedGameDTO) {
        List<BuildingRequestDTO> buildingRequests = new ArrayList<>();
        for (BuildingDTO building : extendedGameDTO.getBuildings()) {
            buildingRequests.add(new BuildingRequestDTO(building.getId(), building.getSolarPanelAmount()));
        }
        //minimizedGameDTO.setBuildingRequests(buildingRequests);
        return MinimizedGameDTO.builder(
                .buildingRequests
        )
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

