package com.dillian.e_mngt_backendforfrontend.dtos;

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.SupervisorDTO;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MinimizedGameDTO {

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
    private final int housing;
    private double goldIncome;
    private double researchIncome;
    private double popularityIncome;
    private String timeOfDay;
    private String weatherType;
    private final List<BuildingRequestDTO> buildingRequests;
    private final List<District> districts;
    private SupervisorDTO supervisor;
}
