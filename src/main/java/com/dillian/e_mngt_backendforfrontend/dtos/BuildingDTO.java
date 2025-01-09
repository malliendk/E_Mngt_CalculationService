package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class BuildingDTO {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String imageUri;
    private final double gridCapacity;
    private final double houseHolds;
    private final double gridLoad;
    private final double energyConsumption;
    private final double solarPanelAmount;
    private final SolarPanelSetDTO solarPanelSet;
    private final double solarPanelCapacity;
    private final double popularityIncome;
    @Setter
    private double energyProduction;
    @Setter
    private double goldIncome;
    @Setter
    private double researchIncome;
    @Setter
    private double environmentalIncome;
}
