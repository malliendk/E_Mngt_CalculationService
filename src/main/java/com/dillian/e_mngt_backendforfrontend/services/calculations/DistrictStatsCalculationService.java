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
        // Initialize the power system with our districts
        powerSystemService.initialize(districts);
        // Calculate power flows in the system
        Map<String, Object> results = powerSystemService.calculatePowerFlows();
        // Log the results
        logResults(results);
        return districts;
    }

    /**
     * Log the power flow calculation results
     * @param results The results of the power flow calculation
     */
    private void logResults(Map<String, Object> results) {
        log.info("Power System Calculation Results:");
        log.info("=================================");
        log.info("Converged: {}, Iterations: {}", results.get("converged"), results.get("iterations"));

        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> districts = (Map<String, Map<String, Object>>) results.get("districts");

        log.info("\nDistrict States:");
        for (String id : districts.keySet()) {
            Map<String, Object> d = districts.get(id);
            log.info("{}:\n  Production={}, Consumption={}, Net={}, Imported={}, Exported={}, " +
                            "Stranded={}, Stress={}, Blackout={}, Cost=${}, Popularity={}",
                    id, d.get("production"), d.get("consumption"), d.get("netProduction"),
                    String.format("%.1f", d.get("injectedPower")), String.format("%.1f", d.get("exportedPower")),
                    String.format("%.1f", d.get("strandedEnergy")), String.format("%.2f", d.get("stressLevel")),
                    d.get("blackout"), String.format("%.0f", d.get("monetaryCost")),
                    String.format("%.1f", d.get("popularityImpact")));
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lines = (List<Map<String, Object>>) results.get("lines");

        log.info("\nLine States:");
        for (Map<String, Object> line : lines) {
            log.info("{}: Capacity={}, Flow={}, Utilization={}%, Overloaded={}",
                    line.get("id"), line.get("capacity"), String.format("%.1f", line.get("flow")),
                    String.format("%.1f", (double)line.get("utilization") * 100), line.get("overloaded"));
        }
    }
}




