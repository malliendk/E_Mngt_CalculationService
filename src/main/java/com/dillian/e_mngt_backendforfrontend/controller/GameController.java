package com.dillian.e_mngt_backendforfrontend.controller;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("game")
@AllArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("start")
    public void startGame(@RequestBody GameDTO gameDTO) {
        gameService.startGame(gameDTO);
    }

    @PutMapping("update")
    public GameDTO updateGame(@RequestBody GameDTO gameDTO) {
        return gameService.updateDTO(gameDTO);
    }
}
