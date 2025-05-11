package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;

import java.util.List;

@Getter
public class InitiateDTO {

    private Long id;
    private List<BuildingRequestDTO> buildingRequests;
    private List<Tile> tiles;
    private List<District> districts;
    private int funds;
    private int popularity;
    private int research;
    private int environmentalScore;
}
