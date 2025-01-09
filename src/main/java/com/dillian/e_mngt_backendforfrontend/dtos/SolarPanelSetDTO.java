package com.dillian.e_mngt_backendforfrontend.dtos;


import com.dillian.e_mngt_backendforfrontend.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
public class SolarPanelSetDTO {

    private final int energyProduction = Constants.SOLAR_PANEL_BASIC_PRODUCTION;
    private final int researchIncome = Constants.SOLAR_PANEL_BASIC_RESEARCH;
    private final int goldIncome = Constants.SOLAR_PANEL_BASIC_GOLD_INCOME;
    private final int environmentIncome = Constants.SOLAR_PANEL_BASIC_ENVIRONMENTAL_INCOME;
}
