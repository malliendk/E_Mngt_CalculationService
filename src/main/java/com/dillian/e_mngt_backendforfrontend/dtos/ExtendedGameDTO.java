package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ExtendedGameDTO {

    private final Long id;
    private double funds;
    private double popularity;
    private double research;
    private final int environmentalScore;
    private int energyProduction;
    private int energyConsumption;
    private double gridLoad;
    private final int gridCapacity;
    private final int solarPanelAmount;
    private final int solarPanelCapacity;
    private final int households;
    private double goldIncome;
    private double researchIncome;
    private double popularityIncome;
    private String timeOfDay;
    private String weatherType;
    private final List<BuildingDTO> buildings;
    private List<District> districts;
    private SupervisorDTO supervisor;


//    public static class GameDTOBuilder {
//
//        private final Map<String, Double> values = new HashMap<>();
//
//        public GameDTOBuilder() {
//        }
//
//        public GameDTOBuilder updateValues(final List<BuildingDTO> buildings) {
//            fieldGetters.entrySet().forEach(getter -> updateField(getter, buildings));
//            return this;
//        }
//
//        private void updateField(final Map.Entry<String, Function<BuildingDTO, Double>> getter, final List<BuildingDTO> buildings) {
//            final double sum = buildings.stream()
//                    .mapToDouble(building -> getter.getValue().apply(building))
//                    .sum();
//            this.values.put(getter.getKey(), sum);
//        }
//
//        public GameDTO build(List<BuildingDTO> buildings, SupervisorDTO supervisor) {
//            return new GameDTO(values, 15L, , buildings, supervisor);
//        }
//    }
}
