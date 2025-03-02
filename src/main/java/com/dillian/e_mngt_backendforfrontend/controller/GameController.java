package com.dillian.e_mngt_backendforfrontend.controller;

import com.dillian.e_mngt_backendforfrontend.GameDTOMapper;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.StartSchedulersService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final GameDTOMapper gameDTOMapper;
    private final StartSchedulersService schedulerService;

    @PostMapping()
    public ResponseEntity<InitiateDTO> startGame(@RequestBody InitiateDTO initiateDTO) {
        log.info("Starting game {}", initiateDTO);
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
}
