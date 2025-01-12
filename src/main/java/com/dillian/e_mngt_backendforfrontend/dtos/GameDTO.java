package com.dillian.e_mngt_backendforfrontend.dtos;

import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.CalculationHelperService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@ToString
public class GameDTO {

    private static final Map<String, Function<BuildingDTO, Double>> fieldGetters = new HashMap<>() {{
        put("totalGridLoad", BuildingDTO::getGridLoad);
        put("gridCapacity", BuildingDTO::getGridCapacity);
        put("energyConsumption", BuildingDTO::getEnergyConsumption);
        put("energyProduction", BuildingDTO::getEnergyProduction);
        put("solarPanelAmount", BuildingDTO::getSolarPanelAmount);
        put("solarPanelCapacity", BuildingDTO::getSolarPanelCapacity);
        put("households", BuildingDTO::getHouseHolds);
        put("goldIncome", BuildingDTO::getGoldIncome);
        put("researchIncome", BuildingDTO::getResearchIncome);
        put("popularityIncome", BuildingDTO::getPopularityIncome);
        put("environmentalIncome", BuildingDTO::getEnvironmentalIncome);
    }};

    private final Map<String, Double> values;
    private final Long id;
    private double environmentalScore;
    private double funds;
    private double popularity;
    private double research;
    private double energyProduction;
    private double energyConsumption;
    private TimeOfDay timeOfDay;
    private WeatherType weatherType;
    private final List<BuildingDTO> buildings;
    private final SupervisorDTO supervisor;

    public GameDTO(final Map<String, Double> values, final Long id, final List<BuildingDTO> buildings, final SupervisorDTO supervisor) {
        this.values = values;
        this.id = id;
        this.buildings = buildings;
        this.supervisor = supervisor;
    }

    public double getTotalGridLoad() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getGridLoad, buildings);    }

    public double getGridCapacity() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getGridCapacity, buildings);    }

    public double getEnergyConsumption() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnergyConsumption, buildings);
    }

    public double getEnergyProduction() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getEnergyProduction, buildings);    }

    public double getSolarPanelAmount() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getSolarPanelAmount, buildings);    }

    public double getSolarPanelCapacity() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getSolarPanelCapacity, buildings);    }

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

    public double getHouseHolds() {
        return CalculationHelperService.sumBuildingPropertyToDouble(BuildingDTO::getHouseHolds, buildings);    }



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
