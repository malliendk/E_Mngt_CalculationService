package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tile {

    private Long id;
    private Long buildingId;
    private BuildingDTO building;
    private Long districtId;
}
