package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.*;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
public class BuildingDTO {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String imageUri;
    private final int gridCapacity;
    private final int houseHolds;
    private final double gridLoad;
    private final double energyProduction;
    private final double energyConsumption;
    private final double goldIncome;
    private final int popularityIncome;
    private final double researchIncome;
    private final double environmentalIncome;
    private final int solarPanelCapacity;
    private final int solarPanelAmount;
    private final SolarPanelSetDTO solarPanelSet;
}
