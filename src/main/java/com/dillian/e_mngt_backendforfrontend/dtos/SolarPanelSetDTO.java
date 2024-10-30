package com.dillian.e_mngt_backendforfrontend.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolarPanelSetDTO {

    private double energyProduction;
    private int researchIncome;
    private int goldIncome;
    private int environmentIncome;
}
