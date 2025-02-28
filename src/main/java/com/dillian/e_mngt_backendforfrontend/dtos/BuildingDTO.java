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
    private final int gridCapacity;
    private final int houseHolds;
    private final int energyConsumption;
    @Setter
    private int solarPanelAmount;
    private final SolarPanelSetDTO solarPanelSet;
    private final int solarPanelCapacity;
    private final int popularityIncome;
    @Setter
    private int energyProduction;
    @Setter
    private int goldIncome;
    @Setter
    private int researchIncome;
    @Setter
    private int environmentalIncome;
}
