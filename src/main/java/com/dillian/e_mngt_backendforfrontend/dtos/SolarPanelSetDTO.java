package com.dillian.e_mngt_backendforfrontend.dtos;


import com.dillian.e_mngt_backendforfrontend.Constants;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class SolarPanelSetDTO {

    public int getEnvironmentIncome() {
        return Constants.SOLAR_PANEL_BASIC_ENVIRONMENTAL_INCOME;
    }

    public int getGoldIncome() {
        return Constants.SOLAR_PANEL_BASIC_GOLD_INCOME;
    }

    public int getResearchIncome() {
        return Constants.SOLAR_PANEL_BASIC_RESEARCH;
    }

    public int getEnergyProduction() {
        return Constants.SOLAR_PANEL_BASIC_PRODUCTION;
    }
}
