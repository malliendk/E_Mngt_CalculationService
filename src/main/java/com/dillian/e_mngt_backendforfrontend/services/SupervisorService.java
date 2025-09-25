package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.utils.constants.ServerURLs;
import com.dillian.e_mngt_backendforfrontend.dtos.SupervisorDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.sumBuildingProperty;

@Service
@AllArgsConstructor
@Slf4j
public class SupervisorService {

    public double processGoldIncome(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        final SupervisorDTO supervisor = initiateDTO.getSupervisor();
        final int baseIncome = sumBuildingProperty(BuildingDTO::getGoldIncome, fullyProcessedBuildings);
        return baseIncome + baseIncome * getIncomeFactor(supervisor.getPerkGoldIncome());
    }

    public double processPopularityIncome(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        final SupervisorDTO supervisor = initiateDTO.getSupervisor();
        final int baseIncome = sumBuildingProperty(BuildingDTO::getPopularityIncome, fullyProcessedBuildings);
        return baseIncome + baseIncome * getIncomeFactor(supervisor.getPerkPopularityIncome());
    }

    public double processResearchIncome(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        final SupervisorDTO supervisor = initiateDTO.getSupervisor();
        final int baseIncome = sumBuildingProperty(BuildingDTO::getResearchIncome, fullyProcessedBuildings);
        log.info("base income research: " + baseIncome);
        return baseIncome + baseIncome * getIncomeFactor(supervisor.getPerkResearchIncome());
    }

    private double getIncomeFactor(int perkLevel) {
        return perkLevel / 20.0;
    }
}
