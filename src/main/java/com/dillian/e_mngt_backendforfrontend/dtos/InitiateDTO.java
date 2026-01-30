package com.dillian.e_mngt_backendforfrontend.dtos;

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.SupervisorDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class InitiateDTO {

    private Long id;
    private SupervisorDTO supervisor;
    private List<Tile> tiles;
    private List<District> districts;
    private List<BuildingRequestDTO> buildingRequests;
    private int funds;
    private int popularity;
    private int research;
    private int environmentalScore;
}
