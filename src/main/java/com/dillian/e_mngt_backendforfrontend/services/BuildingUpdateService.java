package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SolarPanelSetDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BuildingUpdateService {

    private final CalculationHelperService helperService;

    public BuildingDTO mapSolarProductionToBuilding(BuildingDTO buildingDTO) {
        double updatedEnergyProduction = helperService.mapSolarProduction(SolarPanelSetDTO::getEnergyProduction, buildingDTO);
        double updatedGoldIncome = helperService.mapSolarProduction(SolarPanelSetDTO::getGoldIncome, buildingDTO);
        double updatedResearchIncome = helperService.mapSolarProduction(SolarPanelSetDTO::getResearchIncome, buildingDTO);
        double updatedEnvironmentalIncome = helperService.mapSolarProduction(SolarPanelSetDTO::getEnvironmentIncome, buildingDTO);
        return buildingDTO.toBuilder()
                .energyProduction(updatedEnergyProduction)
                .goldIncome(updatedGoldIncome)
                .researchIncome(updatedResearchIncome)
                .environmentalIncome(updatedEnvironmentalIncome)
                .build();
    }

    public BuildingDTO updateSolarBuildingProduction(FactorProvider factorProvider, BuildingDTO building) {
        List<SolarPanelSetDTO> updatedSolarPanelSets = building.getSolarPanelSets().stream()
                .map(solarPanelSetDTO -> helperService.updateSolarPanelProduction(factorProvider, solarPanelSetDTO))
                .toList();
        double updatedProduction = updatedSolarPanelSets.stream()
                .mapToDouble(SolarPanelSetDTO::getEnergyProduction)
                .sum();
        return building.toBuilder()
                .solarPanelSets(updatedSolarPanelSets)
                .energyProduction(updatedProduction)
                .build();
    }

    public BuildingDTO updateEnergySourceProduction(FactorProvider timeOrWeather, BuildingDTO buildingDTO) {
        return helperService.updateBuildingProperty(
                BuildingDTO::getEnergyProduction,
                timeOrWeather.getGenerationFactor(),
                buildingDTO,
                BuildingDTO.BuildingDTOBuilder::energyProduction);
    }

    public BuildingDTO updateHousingConsumption(FactorProvider timeOrWeather, BuildingDTO buildingDTO) {
        return helperService.updateBuildingProperty(
                BuildingDTO::getEnergyConsumption,
                timeOrWeather.getHousingConsumptionFactor(),
                buildingDTO,
                BuildingDTO.BuildingDTOBuilder::energyConsumption);
    }

    public BuildingDTO updateIndustrialConsumption(FactorProvider timeOrWeather, BuildingDTO buildingDTO) {
        return helperService.updateBuildingProperty(
                BuildingDTO::getEnergyConsumption,
                timeOrWeather.getIndustrialConsumptionFactor(),
                buildingDTO,
                BuildingDTO.BuildingDTOBuilder::energyConsumption);
    }

    public BuildingDTO updateGridLoad(BuildingDTO buildingDTO) {
        double updatedGridLoad = Math.abs(buildingDTO.getEnergyProduction() - buildingDTO.getEnergyConsumption());
        return buildingDTO.toBuilder()
                .gridLoad(updatedGridLoad)
                .build();
    }

    //grid capacity
    //
}
