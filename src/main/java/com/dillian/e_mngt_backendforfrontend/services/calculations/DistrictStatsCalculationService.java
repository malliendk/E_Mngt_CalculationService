package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
@Slf4j
public class DistrictStatsCalculationService {


    private final PowerSystemService powerSystemService;

    /**
     * Calculates cumulative values for all districts in the game.
     * @param districts The list of districts to process.
     * @return a list of fully processed Districts.
     */
    public List<District> calculateCumulativeDistrictValues(List<District> districts) {
        powerSystemService.initialize(districts);
        powerSystemService.calculatePowerFlows();
        return districts;
    }
}




