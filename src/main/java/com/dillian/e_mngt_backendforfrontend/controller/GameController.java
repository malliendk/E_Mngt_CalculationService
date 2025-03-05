package com.dillian.e_mngt_backendforfrontend.controller;

import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.GameEventService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.StartSchedulersService;
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
    private final StartSchedulersService schedulerService;
    private final GameEventService gameEventService;

    @PostMapping()
    public ResponseEntity<InitiateDTO> startGame(@RequestBody InitiateDTO initiateDTO) {
        final MinimizedGameDTO minimizedGameDTO = gameService.buildGameDTO(initiateDTO);
        schedulerService.startSchedulers(minimizedGameDTO);
        return ResponseEntity.ok(initiateDTO);
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

    /**
     * SSE endpoint for subscribing to real-time game updates
     * @return SSE emitter for streaming game data
     */
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToGameEvents() {
        log.info("New client subscribing to game events");
        return gameEventService.subscribe();
    }
}
