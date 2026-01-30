package com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class District {
    private Long id;
    private int energyProduction;
    private int energyConsumption;
    private int gridCapacity;
    private int goldIncome;
    private int popularityIncome;
    private int researchIncome;
    private int environmentalScore;
    private int solarPanelCapacity;
    private int solarPanelAmount;
    private int housing;
    private List<Tile> tiles;

    private int netProduction;
    private double injectedPower;       // Power entering the district
    private double exportedPower;       // Power leaving the district
    private double strandedEnergy;      // Excess energy with nowhere to go
    private double stressLevel;         // Current stress on district grid
    private List<String> connectedLines; // IDs of connected transmission lines

    private double energyDeficit;
    private double demandCoverage;
    private boolean blackout;            // True if stressLevel > 0.5
    private double monetaryCost;         // Financial impact of stress
    private double popularityImpact;     // Political impact of stress
    private int distributedEnergyProduction;    // Energy from housing and public buildings
    private int centralizedEnergyProduction;    // Energy from power plants, etc.
    private int localConsumption;               // Consumption from housing and public buildings
    private int strandedLocalEnergy;            // Local energy that can't enter the grid due to congestion

    public void setEnergyProduction(int energyProduction) {
        this.energyProduction = energyProduction;
        this.netProduction = this.energyProduction - this.energyConsumption;
    }

    public void setEnergyConsumption(int energyConsumption) {
        this.energyConsumption = energyConsumption;
        this.netProduction = this.energyProduction - this.energyConsumption;
    }

    public void setStressLevel(double stressLevel) {
        this.stressLevel = stressLevel;
        this.blackout = stressLevel > 0.5;
    }
}