package com.dillian.e_mngt_backendforfrontend.dtos;

import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Builder(toBuilder = true)
public class GameDTO {

    private final Long id;
    private final double totalGridLoad;
    private final double environmentalScore;
    private final double funds;
    private final double popularity;
    private final int gridCapacity;
    private final double distributionEfficiency;
    private final int households;
    private final int publicBuildingSolarPanelCapacity;
    private final double energyConsumption;
    private final double energyProduction;
    private final double research;
    private final double goldIncome;
    private final double researchIncome;
    private final double popularityIncome;
    private final double environmentalIncome;
    private final List<BuildingDTO> buildings;
    private final SupervisorDTO supervisor;
    private final TimeOfDay timeOfDay;
    private final WeatherType weatherType;
}
