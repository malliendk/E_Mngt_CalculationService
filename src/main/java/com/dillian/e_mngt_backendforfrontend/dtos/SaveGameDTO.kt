package com.dillian.e_mngt_backendforfrontend.dtos

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.SupervisorDTO
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.Tile
import java.time.LocalDateTime

data class SaveGameDTO (
    val id: Long,
    var supervisor: SupervisorDTO,
    val savedAt: LocalDateTime = LocalDateTime.now(),
    val tiles: List<Tile>,
    val districts: List<District>,
    val buildingRequests: List<BuildingRequestDTO>,
    val funds: Int,
    val popularity: Int,
    val research: Int,
    val environmentalScore: Int
)