package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class MinimizedGameDTO {

    private final Long id;
    private int funds;
    private int popularity;
    private int research;
    private final int environmentalScore;
    private int energyProduction;
    private int energyConsumption;
    private double gridLoad;
    private final int gridCapacity;
    private final int solarPanelAmount;
    private final int solarPanelCapacity;
    private final int households;
    private final int goldIncome;
    private final int researchIncome;
    private final int popularityIncome;
    private final int environmentalIncome;
    private String timeOfDay;
    private String weatherType;
    private final List<BuildingRequestDTO> buildingRequests;
}
