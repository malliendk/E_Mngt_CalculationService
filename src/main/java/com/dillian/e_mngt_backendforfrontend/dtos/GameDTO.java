package com.dillian.e_mngt_backendforfrontend.dtos;

import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.*;
import org.antlr.v4.runtime.misc.DoubleKeyMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class GameDTO {

    private static final Map<String, Function<BuildingDTO, Double>> fieldGetters = Map.of(
            "gridLoad", BuildingDTO::getGridLoad,
            "environmentalScore", BuildingDTO::getEnvironmentalIncome
    ) ;

    private final Map<String, Double> values;

    private final Long id;
//    private Double totalGridLoad;
//    private double environmentalScore;
//    private double funds;
//    private double popularity;
//    private double gridCapacity;
//    private double distributionEfficiency;
//    private int households;
//    private double energyConsumption;
//    private double energyProduction;
//    private double research;
//    private double goldIncome;
//    private double researchIncome;
//    private double popularityIncome;
//    private double environmentalIncome;
//    private double solarPanelCapacity;
//    private int solarPanelTotalAmount;
    private List<BuildingDTO> buildings;
    private SupervisorDTO supervisor;
    private TimeOfDay timeOfDay;
    private WeatherType weatherType;

    public double getTotalGridLoad() {
/*        if (totalGridLoad == null) {
            totalGridLoad = buildings.stream()
                    .mapToDouble(BuildingDTO::getGridLoad)
                    .sum();
        }*/
        return buildings.stream()
                .mapToDouble(BuildingDTO::getGridLoad)
                .sum();
    }


    public static class GameDTOBuilder {

        private Map<String, Double> values = new HashMap<>();

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

        public GameDTO build() {
            return new GameDTO(values, 15L);
        }
    }

}
