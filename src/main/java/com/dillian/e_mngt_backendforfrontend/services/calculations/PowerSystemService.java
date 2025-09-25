package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.District;
import com.dillian.e_mngt_backendforfrontend.dtos.TransmissionLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;


import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.sumBuildingProperty;


@Service
@Slf4j
public class PowerSystemService {

    private static final double TRANSMISSION_LOSS_FACTOR = 0.03; // 3% loss per unit resistance
    private static final int MAX_ITERATIONS = 20; // Maximum iterations for convergence

    private Map<String, District> districts; // All districts in the system
    private List<TransmissionLine> lines;    // All transmission lines

    /**
     * Initialize the power system, setting up districts and transmission lines
     * based on spatial arrangement
     */
    public void initialize(List<District> districtList) {
        // Initialize the district map and line list
        this.districts = new HashMap<>();
        this.lines = new ArrayList<>();

        // Calculate individual district values
        calculateIndividualDistrictValues(districtList);

        // Add all districts to the system
        for (District district : districtList) {
            String districtId = district.getId().toString();
            district.setConnectedLines(new ArrayList<>());
            districts.put(districtId, district);
        }

        // Create automatic transmission lines based on district positions
        Map<District, Point> districtPositions = mapDistrictsToPositions(districtList);
        createTransmissionLines(districtList, districtPositions);
    }

    /**
     * Calculate power flows across the entire system
     * @return A map containing the calculation results
     */
    public Map<String, Object> calculatePowerFlows() {
        // Reset all flows to starting condition
        resetFlows();

        // Initial district balance calculation
        for (District district : districts.values()) {
            district.setInjectedPower(0);
            district.setExportedPower(0);
            district.setStrandedEnergy(0);
            district.setStressLevel(0);
        }

        // Iterative approximation of power flows
        int iteration = 0;
        boolean systemConverged = false;

        while (!systemConverged && iteration < MAX_ITERATIONS) {
            systemConverged = true;

            // Calculate power distribution based on current state
            for (District district : districts.values()) {
                double excessPower = district.getNetProduction() - district.getExportedPower() + district.getInjectedPower();

                if (Math.abs(excessPower) > 0.01) { // If there's still power to distribute
                    systemConverged = false;

                    if (excessPower > 0) {
                        // This district needs to export power
                        distributeExcessPower(district, excessPower);
                    }
                }
            }

            iteration++;
        }
        calculateStressLevels();

        Map<String, Object> results = new HashMap<>();
        results.put("converged", systemConverged);
        results.put("iterations", iteration);
        results.put("districts", getDistrictStates());
        results.put("lines", getLineStates());

        return results;
    }

    /**
     * Distribute excess power from a district to connected districts
     * @param district The district with excess power
     * @param excessPower The amount of excess power to distribute
     */
    private void distributeExcessPower(District district, double excessPower) {
        String districtId = district.getId().toString();

        // Get all lines connected to this district
        List<TransmissionLine> connectedLines = district.getConnectedLines().stream()
                .map(id -> lines.stream()
                        .filter(line -> line.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // Calculate available capacity on each outgoing line
        List<TransmissionLine> outgoingLines = connectedLines.stream()
                .filter(line -> line.getFrom().equals(districtId) && line.getFlow() < line.getCapacity())
                .toList();

        if (outgoingLines.isEmpty()) {
            // Nowhere to send power, it becomes stranded
            district.setStrandedEnergy(district.getStrandedEnergy() + excessPower);
            return;
        }

        // Calculate distribution weights based on available capacity and resistance
        double totalWeight = outgoingLines.stream()
                .mapToDouble(line -> (line.getCapacity() - line.getFlow()) / line.getResistance())
                .sum();

        if (totalWeight == 0) {
            district.setStrandedEnergy(district.getStrandedEnergy() + excessPower);
            return;
        }

        // Distribute power based on weights
        for (TransmissionLine line : outgoingLines) {
            double lineWeight = (line.getCapacity() - line.getFlow()) / line.getResistance() / totalWeight;
            double powerToSend = Math.min(
                    excessPower * lineWeight,
                    line.getCapacity() - line.getFlow()
            );

            if (powerToSend > 0) {
                line.setFlow(line.getFlow() + powerToSend);
                district.setExportedPower(district.getExportedPower() + powerToSend);

                // Add power to receiving district
                District receivingDistrict = districts.get(line.getTo());
                double powerReceived = powerToSend * (1 - (line.getResistance() * TRANSMISSION_LOSS_FACTOR));
                receivingDistrict.setInjectedPower(receivingDistrict.getInjectedPower() + powerReceived);
            }
        }
    }

    private void calculateStressLevels() {
        for (District district : districts.values()) {
            // Total power in the district: netProduction + injectedPower - exportedPower
            double totalPowerInDistrict = district.getNetProduction() + district.getInjectedPower() - district.getExportedPower();

            // Preserve any existing stranded energy from power distribution phase and local congestion
            double existingStrandedEnergy = district.getStrandedEnergy();

            // Check if district is overloaded (exceeding grid capacity)
            if (totalPowerInDistrict > district.getGridCapacity()) {
                // Additional stranded energy due to exceeding grid capacity
                double additionalStrandedEnergy = totalPowerInDistrict - district.getGridCapacity();
                district.setStrandedEnergy(existingStrandedEnergy + additionalStrandedEnergy);
                // Set blackout flag if severely overloaded
                district.setBlackout(true);
            } else {
                // No blackout from grid overload
                district.setBlackout(false);
            }

            // Calculate stress level based on absolute stranded energy, not relative to capacity
            // Stress should be based on the actual impact of stranded energy, not grid size

            // Define stress levels based on absolute stranded energy amounts
            double stressLevel = getStressLevel(district);

            district.setStressLevel(Math.min(1.0, stressLevel)); // Cap at 1.0

            // Calculate monetary cost and popularity impact
            district.setMonetaryCost(calculateMonetaryCost(district.getStressLevel()));
            district.setPopularityImpact(calculatePopularityImpact(district.getStressLevel()));
        }
    }

    private static double getStressLevel(final District district) {
        double strandedEnergy = district.getStrandedEnergy();
        double stressLevel = 0.0;

        if (strandedEnergy > 0) {
            // Progressive stress calculation based on absolute stranded energy
            // These thresholds can be adjusted based on your game balance
            if (strandedEnergy >= 100000) {
                // Very high stress for massive stranded energy
                stressLevel = 0.8 + Math.min(0.2, (strandedEnergy - 100000) / 100000 * 0.2);
            } else if (strandedEnergy >= 50000) {
                // High stress
                stressLevel = 0.5 + (strandedEnergy - 50000) / 50000 * 0.3;
            } else if (strandedEnergy >= 20000) {
                // Moderate stress
                stressLevel = 0.3 + (strandedEnergy - 20000) / 30000 * 0.2;
            } else if (strandedEnergy >= 5000) {
                // Low stress
                stressLevel = 0.1 + (strandedEnergy - 5000) / 15000 * 0.2;
            } else {
                // Minimal stress
                stressLevel = strandedEnergy / 5000 * 0.1;
            }
        }

        // Additional stress from blackout condition
        if (district.isBlackout()) {
            stressLevel = Math.max(stressLevel, 0.8);
        }
        return stressLevel;
    }


    /**
     * Reset all flows to zero
     */
    private void resetFlows() {
        for (TransmissionLine line : lines) {
            line.setFlow(0);
        }
    }

    /**
     * Create transmission lines based on district positions
     * @param districtList List of all districts
     * @param districtPositions Map of districts to their positions
     */
    private void createTransmissionLines(List<District> districtList, Map<District, Point> districtPositions) {
        // Create a standard capacity based on average district grid capacity
        int averageCapacity = districtList.stream()
                .mapToInt(District::getGridCapacity)
                .sum() / districtList.size();

        int defaultCapacity = averageCapacity / 2; // Default line capacity is half average district capacity

        // Find adjacent districts and create lines between them
        for (int i = 0; i < districtList.size(); i++) {
            District current = districtList.get(i);
            String currentId = current.getId().toString();

            for (int j = i + 1; j < districtList.size(); j++) {
                District other = districtList.get(j);
                String otherId = other.getId().toString();

                Point currentPos = districtPositions.get(current);
                Point otherPos = districtPositions.get(other);

                // Two districts are adjacent if they differ by 1 in exactly one coordinate
                // and are the same in the other coordinate (sharing an edge)
                int xDiff = Math.abs(currentPos.x - otherPos.x);
                int yDiff = Math.abs(currentPos.y - otherPos.y);

                if ((xDiff == 1 && yDiff == 0) || (xDiff == 0 && yDiff == 1)) {
                    // Create transmission line in both directions
                    TransmissionLine forwardLine = new TransmissionLine(currentId, otherId, defaultCapacity, 1.0);
                    TransmissionLine backwardLine = new TransmissionLine(otherId, currentId, defaultCapacity, 1.0);

                    lines.add(forwardLine);
                    lines.add(backwardLine);

                    current.getConnectedLines().add(forwardLine.getId());
                    other.getConnectedLines().add(backwardLine.getId());
                }
            }
        }
    }

    /**
     * Get the current state of all districts
     * @return Map of district states
     */
    private Map<String, Map<String, Object>> getDistrictStates() {
        Map<String, Map<String, Object>> result = new HashMap<>();

        for (Map.Entry<String, District> entry : districts.entrySet()) {
            District district = entry.getValue();
            Map<String, Object> districtData = new HashMap<>();

            districtData.put("id", district.getId());
            districtData.put("production", district.getEnergyProduction());
            districtData.put("distributedProduction", district.getDistributedEnergyProduction());
            districtData.put("centralizedProduction", district.getCentralizedEnergyProduction());
            districtData.put("consumption", district.getEnergyConsumption());
            districtData.put("localConsumption", district.getLocalConsumption());
            districtData.put("gridCapacity", district.getGridCapacity());
            districtData.put("netProduction", district.getNetProduction());
            districtData.put("injectedPower", district.getInjectedPower());
            districtData.put("exportedPower", district.getExportedPower());
            districtData.put("strandedEnergy", district.getStrandedEnergy());
            districtData.put("strandedLocalEnergy", district.getStrandedLocalEnergy());
            districtData.put("stressLevel", district.getStressLevel());
            districtData.put("blackout", district.isBlackout());
            districtData.put("monetaryCost", district.getMonetaryCost());
            districtData.put("popularityImpact", district.getPopularityImpact());

            result.put(entry.getKey(), districtData);
        }

        return result;
    }


    /**
     * Get the current state of all transmission lines
     * @return List of line states
     */
    private List<Map<String, Object>> getLineStates() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (TransmissionLine line : lines) {
            Map<String, Object> lineData = new HashMap<>();

            lineData.put("id", line.getId());
            lineData.put("from", line.getFrom());
            lineData.put("to", line.getTo());
            lineData.put("capacity", line.getCapacity());
            lineData.put("flow", line.getFlow());
            lineData.put("utilization", line.getUtilization());
            lineData.put("overloaded", line.isOverloaded());

            result.add(lineData);
        }

        return result;
    }

    /**
     * Maps each district to its position in the grid.
     * Districts are arranged in a 2x2 grid initially, with additional rows of 2 added below.
     * @param districts The list of districts to be mapped.
     * @return A map of districts to their positions in the grid.
     */
    private Map<District, Point> mapDistrictsToPositions(List<District> districts) {
        Map<District, Point> positions = new HashMap<>();
        for (int i = 0; i < districts.size(); i++) {
            int x = i % 2;      // 0 for left column, 1 for right column
            int y = i / 2;      // Row number (0-indexed)
            positions.put(districts.get(i), new Point(x, y));
        }
        return positions;
    }

    /**
     * Calculates individual values for each district, such as energy production, energy consumption, and grid capacity.
     * @param districts The list of districts to process.
     */
    private void calculateIndividualDistrictValues(List<District> districts) {
        for (District district : districts) {
            List<BuildingDTO> districtBuildings = district.getTiles().stream()
                    .map(tile -> tile.getBuilding())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            final int energyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, districtBuildings);
            final int energyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, districtBuildings);
            final int gridCapacity = sumBuildingProperty(BuildingDTO::getGridCapacity, districtBuildings);

            district.setEnergyProduction(energyProduction);
            district.setEnergyConsumption(energyConsumption);
            district.setGridCapacity(gridCapacity);
            district.setNetProduction(energyProduction - energyConsumption);
        }
    }

    /**
     * Calculate monetary cost from stress level
     */
    private double calculateMonetaryCost(double stressLevel) {
        if (stressLevel <= 0) return 0;

        int baseCost = 1000; // Base cost for any stress

        if (stressLevel > 0.5) {
            // Blackout condition - severe costs
            return baseCost * (5 + stressLevel * 10);
        } else if (stressLevel > 0.3) {
            // Severe stress - high costs
            return baseCost * (2 + stressLevel * 5);
        } else if (stressLevel > 0.1) {
            // Moderate stress
            return baseCost * (1 + stressLevel * 3);
        } else {
            // Minor stress
            return baseCost * stressLevel;
        }
    }

    /**
     * Calculate popularity impact from stress level
     */
    private double calculatePopularityImpact(double stressLevel) {
        if (stressLevel <= 0) return 0;

        if (stressLevel > 0.5) {
            // Blackout condition
            return -10 - stressLevel * 20;
        } else if (stressLevel > 0.3) {
            // Severe stress
            return -5 - stressLevel * 10;
        } else if (stressLevel > 0.1) {
            // Moderate stress
            return -2 - stressLevel * 5;
        } else {
            // Minor stress
            return -stressLevel * 3;
        }
    }
}



