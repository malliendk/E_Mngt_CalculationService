package com.dillian.e_mngt_backendforfrontend.controller;

import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.StartSchedulersService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final StartSchedulersService schedulerService;

    @PostMapping()
    public void startGame(@RequestBody InitiateDTO initiateDTO) {
        log.info("Starting game {}", initiateDTO);
        gameService.buildGameDTO(initiateDTO);
        schedulerService.startSchedulers();
    }

    @GetMapping
    public ExtendedGameDTO getGameDto() {
        return gameService.getExtendedGameDTO();
    }

    @PutMapping()
    public ExtendedGameDTO updateGame(@RequestBody InitiateDTO initiateDTO) {
        return gameService.buildGameDTO(initiateDTO);
    }
}
