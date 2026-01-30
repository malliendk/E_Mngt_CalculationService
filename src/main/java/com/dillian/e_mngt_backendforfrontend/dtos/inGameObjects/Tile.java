package com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Tile {

    private Long id;
    private Long buildingId;
    private BuildingDTO building;
    private Long districtId;
}
