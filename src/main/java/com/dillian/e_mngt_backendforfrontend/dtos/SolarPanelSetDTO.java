package com.dillian.e_mngt_backendforfrontend.dtos;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
public class SolarPanelSetDTO {

    private final double energyProduction;
    private final double researchIncome;
    private final double goldIncome;
    private final double environmentIncome;
}
