package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BuildingRequestDTO {

    private Long buildingId;
    private int solarPanelAmount;
}