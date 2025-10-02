package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BuildingDTO {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String imageUri;
    private final int gridCapacity;
    private final int housing;
    private int energyProduction;
    private final int energyConsumption;
    private int solarPanelAmount;
    private final int solarPanelCapacity;
    private int popularityIncome;
    private int goldIncome;
    private int researchIncome;
    private int environmentalScore;
    private int housingRequirement;
}
