package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BuildingUpdateService {

    public BuildingDTO mapSolarProductionToBuilding(BuildingDTO buildingDTO) {
        SolarPanelSetDTO solarPanelSet = buildingDTO.getSolarPanelSet();
        int solarPanelAmount = buildingDTO.getSolarPanelAmount();
        double updatedEnergyProduction = solarPanelSet.getEnergyProduction() * solarPanelAmount;
        double updatedGoldIncome = solarPanelSet.getGoldIncome() * solarPanelAmount;
        double updatedResearchIncome = solarPanelSet.getResearchIncome() * solarPanelAmount;
        double updatedEnvironmentalIncome = solarPanelSet.getEnvironmentIncome() * solarPanelAmount;
        return buildingDTO.toBuilder()
                .energyProduction(updatedEnergyProduction)
                .goldIncome(updatedGoldIncome)
                .researchIncome(updatedResearchIncome)
                .environmentalIncome(updatedEnvironmentalIncome)
                .build();
    }

    public BuildingDTO updateEnergyProduction(FactorProvider factorProvider, BuildingDTO building) {
        double updatedProduction = building.getEnergyProduction() * factorProvider.getGenerationFactor();
        return building.toBuilder()
                .energyProduction(updatedProduction)
                .build();
    }

    public BuildingDTO updateHousingEnergyConsumption(TimeOfDay timeOfDay, BuildingDTO buildingDTO) {
        double updatedConsumption = buildingDTO.getEnergyConsumption() * timeOfDay.getHousingConsumptionFactor();
        return buildingDTO.toBuilder()
                .energyConsumption(updatedConsumption)
                .build();
    }

    public BuildingDTO updateIndustrialConsumption(TimeOfDay timeOfDay, BuildingDTO buildingDTO) {
        double updatedConsumption = buildingDTO.getEnergyConsumption() * timeOfDay.getIndustrialConsumptionFactor();
        return buildingDTO.toBuilder()
                .energyConsumption(updatedConsumption)
                .build();
    }

    public BuildingDTO updateGridLoad(BuildingDTO buildingDTO) {
        double updatedGridLoad = Math.abs(buildingDTO.getEnergyProduction() - buildingDTO.getEnergyConsumption());
        return buildingDTO.toBuilder()
                .gridLoad(updatedGridLoad)
                .build();
    }
}
