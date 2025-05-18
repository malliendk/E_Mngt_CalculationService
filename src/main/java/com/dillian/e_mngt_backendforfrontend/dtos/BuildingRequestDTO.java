package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BuildingRequestDTO {

    private Long buildingId;
    private int solarPanelAmount;
    private Map<String, Object> propertiesMap;
}