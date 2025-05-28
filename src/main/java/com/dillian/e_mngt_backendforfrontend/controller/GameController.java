package com.dillian.e_mngt_backendforfrontend.controller;

import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.GameEventService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.SchedulerUpdateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {

    private final GameService gameService;
    private final GameEventService gameEventService;
    private final SchedulerUpdateService schedulerService;

    @PostMapping()
    public ResponseEntity<InitiateDTO> startGame(@RequestBody InitiateDTO initiateDTO) {
        gameService.buildGameDTO(initiateDTO);
        schedulerService.startSchedulers();
        return ResponseEntity.ok(initiateDTO);
    }

    @PostMapping("schedulers/shutdown")
    public ResponseEntity<Void> shutdownSchedulers() {
        schedulerService.shutdownSchedulers();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<MinimizedGameDTO> getGameDto() {
        final ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
        final MinimizedGameDTO minimizedGameDTO = gameService.minimizeGameDTO(extendedGameDTO);
        return ResponseEntity.ok(minimizedGameDTO);
    }

    @PutMapping()
    public ResponseEntity<InitiateDTO> updateGame(@RequestBody InitiateDTO initiateDTO) {
        gameService.buildGameDTO(initiateDTO);
        return ResponseEntity.ok(initiateDTO);
    }


    @GetMapping(value = "/income-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamIncomeUpdates() {
        log.info("SSE endpoint called for income stream");
        return gameEventService.createIncomeStream();
    }

    @GetMapping(value = "/day-weather-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamdayWeatherUpdates() {
        log.info("SSE endpoint called for day-weather stream");
        return gameEventService.createDayWeatherStream();
    }}




