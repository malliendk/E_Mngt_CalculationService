package com.dillian.e_mngt_backendforfrontend.controller;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.services.schedulers.SchedulerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final SchedulerService schedulerService;

    @PostMapping("start")
    public void startGame(@RequestBody GameDTO gameDTO) {
        gameService.buildGameDTO(gameDTO);
        schedulerService.startSchedulers();
    }

    @GetMapping
    public GameDTO getGameDto() {
        return gameService.getGameDTO();
    }

//    @PutMapping("update")
//    public GameDTO updateGame(@RequestBody GameDTO gameDTO) {
//        return gameService.updateDTO(gameDTO);
//    }
}
