package com.dillian.e_mngt_backendforfrontend.controller

import com.dillian.e_mngt_backendforfrontend.dtos.SaveGameDTO
import com.dillian.e_mngt_backendforfrontend.services.GameDTOBuilderService
import com.dillian.e_mngt_backendforfrontend.services.GameService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/save-game")
class SaveGameController (
    private val gameService: GameService,
    private val builderService: GameDTOBuilderService) {

    @PostMapping("load")
    fun loadGame(@RequestBody savedGame: SaveGameDTO) {
        val initiateDTO = builderService.mapToInitiateDTO(savedGame)
        gameService.buildGameDTO(initiateDTO);
    }
}