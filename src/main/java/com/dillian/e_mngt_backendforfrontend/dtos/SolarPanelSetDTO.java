package com.dillian.e_mngt_backendforfrontend.dtos;


import com.dillian.e_mngt_backendforfrontend.constants.SolarPanelValues;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class SolarPanelSetDTO {

    public int getEnvironmentScore() {
        return SolarPanelValues.SOLAR_PANEL_BASIC_ENVIRONMENTAL_SCORE;
    }

    public int getGoldIncome() {
        return SolarPanelValues.SOLAR_PANEL_BASIC_GOLD_INCOME;
    }

    public int getResearchIncome() {
        return SolarPanelValues.SOLAR_PANEL_BASIC_RESEARCH;
    }

    public int getEnergyProduction() {
        return SolarPanelValues.SOLAR_PANEL_BASIC_PRODUCTION;
    }
}
