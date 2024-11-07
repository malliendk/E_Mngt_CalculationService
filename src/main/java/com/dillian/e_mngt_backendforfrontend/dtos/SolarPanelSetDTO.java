package com.dillian.e_mngt_backendforfrontend.dtos;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class SolarPanelSetDTO {

    private final double energyProduction;
    private final double researchIncome;
    private final double goldIncome;
    private final double environmentIncome;
}
