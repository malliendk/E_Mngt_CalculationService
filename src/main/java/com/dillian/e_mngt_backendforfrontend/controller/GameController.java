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
        log.info("environmentalScore: {}", extendedGameDTO.getEnvironmentalScore());
        return ResponseEntity.ok(minimizedGameDTO);
    }

    @PutMapping()
    public ResponseEntity<InitiateDTO> updateGame(@RequestBody InitiateDTO initiateDTO) {
        gameService.buildGameDTO(initiateDTO);
        return ResponseEntity.ok(initiateDTO);
    }


    @GetMapping("/income")
    public SseEmitter streamIncome() {
        return gameEventService.subscribeToIncome();
    }

    @GetMapping("/weather")
    public SseEmitter streamWeather() {
        return gameEventService.subscribeToWeather();
    }
}
