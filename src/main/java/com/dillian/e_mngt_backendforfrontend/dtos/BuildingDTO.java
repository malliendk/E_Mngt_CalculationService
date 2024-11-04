package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuildingDTO {

    private Long id;
    private String name;
    private String description;
    private int price;
    private String imageUri;
    private int gridCapacity;
    private double gridLoad;
    private double energyProduction;
    private int houseHolds;
    private double energyConsumption;
    private double goldIncome;
    private int popularityIncome;
    private double researchIncome;
    private double environmentalIncome;
    private List<SolarPanelSetDTO> solarPanelSets;

}
