package com.dillian.e_mngt_backendforfrontend.dtos;

import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.CalculationHelperService;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@ToString
public class GameDTO {

    private static final Map<String, Function<BuildingDTO, Double>> fieldGetters = Map.of(
            "totalGridLoad", BuildingDTO::getGridLoad,
            "gridCapacity", BuildingDTO::getGridCapacity,
            "energyConsumption", BuildingDTO::getEnergyConsumption,
            "energyProduction", BuildingDTO::getEnergyProduction,
            "solarPanelAmount", BuildingDTO::getSolarPanelAmount,
            "solarPanelCapacity", BuildingDTO::getSolarPanelCapacity,
            "goldIncome", BuildingDTO::getGoldIncome,
            "researchIncome", BuildingDTO::getResearchIncome,
            "popularityIncome", BuildingDTO::getPopularityIncome,
            "environmentalIncome", BuildingDTO::getEnvironmentalIncome
    );

    private final Map<String, Double> values;
    private final Long id;
    @Setter
    private double environmentalScore;
    @Setter
    private double funds;
    @Setter
    private double popularity;
    @Setter
    private double research;
    @Setter
    private double energyProduction;
    @Setter
    private double energyConsumption;
    private final List<BuildingDTO> buildings;
    private final SupervisorDTO supervisor;
    @Setter
    private TimeOfDay timeOfDay;
    @Setter
    private WeatherType weatherType;

    public GameDTO(final Map<String, Double> values, final Long id, final List<BuildingDTO> buildings, final SupervisorDTO supervisor) {
        this.values = values;
        this.id = id;
        this.buildings = buildings;
        this.supervisor = supervisor;
    }

    public double getGoldIncome() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getGoldIncome, buildings);
    }

    public double getResearchIncome() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getResearchIncome, buildings);
    }

    public double getPopularityIncome() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getPopularityIncome, buildings);
    }

    public double getEnvironmentalIncome() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnvironmentalIncome, buildings);
    }


    public static class GameDTOBuilder {

        private final Map<String, Double> values = new HashMap<>();

        public GameDTOBuilder() {
        }

        public GameDTOBuilder updateValues(final List<BuildingDTO> buildings) {
            fieldGetters.entrySet().forEach(getter -> updateField(getter, buildings));
            return this;
        }

        private void updateField(final Map.Entry<String, Function<BuildingDTO, Double>> getter, final List<BuildingDTO> buildings) {
            final double sum = buildings.stream()
                    .mapToDouble(building -> getter.getValue().apply(building))
                    .sum();
            this.values.put(getter.getKey(), sum);
        }

        public GameDTO build(List<BuildingDTO> buildings, SupervisorDTO supervisor) {
            return new GameDTO(values, 15L, buildings, supervisor);
        }
    }
}
