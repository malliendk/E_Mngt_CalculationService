package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class BuildingDTO {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String imageUri;
    private final int gridCapacity;
    private final double gridLoad;
    private final double energyProduction;
    private final int houseHolds;
    private final double energyConsumption;
    private final double goldIncome;
    private final int popularityIncome;
    private final double researchIncome;
    private final double environmentalIncome;
    private final List<SolarPanelSetDTO> solarPanelSets;

}
