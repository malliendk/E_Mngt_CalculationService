package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.District;
import com.dillian.e_mngt_backendforfrontend.dtos.TransmissionLine;
import org.springframework.stereotype.Service;

import java.awt.Point;
import java.util.stream.Collectors;

import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.sumBuildingProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PowerSystemService {

    private static final double TRANSMISSION_LOSS_FACTOR = 0.03; // 3% loss per unit resistance
    private static final int MAX_ITERATIONS = 20; // Maximum iterations for convergence
    private static final int BASE_MONETARY_COST = 100; // Base cost for monetary calculations, reduced for game balance
    private static final int TOTAL_GOLD_CAP = 1000; // Cap total monetary cost at 20% of starting 5000 gold
    private static final double BASE_POPULARITY_IMPACT = 1.0; // Base for popularity calculations

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
            district.setEnergyDeficit(0);
            district.setDemandCoverage(1.0);
            district.setStressLevel(0);
            district.setBlackout(false);
        }

        // First pass: identify districts with deficits and surpluses
        Map<String, Double> initialBalances = new HashMap<>();
        for (District district : districts.values()) {
            double balance = district.getNetProduction();
            initialBalances.put(district.getId().toString(), balance);
            if (balance < 0) {
                // This district has a deficit
                district.setEnergyDeficit(Math.abs(balance));
                district.setDemandCoverage(district.getEnergyProduction() / (double) district.getEnergyConsumption());
            }
        }

        // Iterative approximation of power flows
        int iteration = 0;
        boolean systemConverged = false;

        while (!systemConverged && iteration < MAX_ITERATIONS) {
            systemConverged = true;

            // First priority: Try to supply districts with deficits
            for (District district : districts.values()) {
                if (district.getEnergyDeficit() > 0) {
                    double remainingDeficit = attemptToImportPower(district, district.getEnergyDeficit());
                    if (Math.abs(remainingDeficit - district.getEnergyDeficit()) > 0.01) {
                        systemConverged = false;
                        district.setEnergyDeficit(remainingDeficit);
                        if (district.getEnergyConsumption() > 0) {
                            double actualPowerAvailable = district.getEnergyProduction() + district.getInjectedPower();
                            district.setDemandCoverage(Math.min(1.0, actualPowerAvailable / district.getEnergyConsumption()));
                        }
                    }
                }
            }

            // Second priority: Distribute excess power
            for (District district : districts.values()) {
                double excessPower = district.getNetProduction() - district.getExportedPower() + district.getInjectedPower();

                if (Math.abs(excessPower) > 0.01 && excessPower > 0) {
                    systemConverged = false;
                    distributeExcessPower(district, excessPower);
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
     * Attempt to import power to a district with deficit
     * @param district The district needing power
     * @param deficitAmount The amount of power needed
     * @return Remaining deficit after imports
     */
    private double attemptToImportPower(District district, double deficitAmount) {
        String districtId = district.getId().toString();

        // Get all incoming lines that could supply power
        List<TransmissionLine> incomingLines = district.getConnectedLines().stream()
                .map(id -> lines.stream()
                        .filter(line -> line.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .filter(line -> line.getTo().equals(districtId))
                .toList();

        if (incomingLines.isEmpty()) {
            return deficitAmount; // No way to import power
        }

        double remainingDeficit = deficitAmount;

        for (TransmissionLine line : incomingLines) {
            District sourceDistrict = districts.get(line.getFrom());

            // Check if source has excess power
            double sourceExcess = sourceDistrict.getNetProduction() + sourceDistrict.getInjectedPower()
                    - sourceDistrict.getExportedPower();

            if (sourceExcess > 0) {
                double availableCapacity = line.getCapacity() - line.getFlow();
                double powerToTransfer = Math.min(Math.min(sourceExcess, remainingDeficit), availableCapacity);

                if (powerToTransfer > 0) {
                    line.setFlow(line.getFlow() + powerToTransfer);
                    sourceDistrict.setExportedPower(sourceDistrict.getExportedPower() + powerToTransfer);

                    double powerReceived = powerToTransfer * (1 - (line.getResistance() * TRANSMISSION_LOSS_FACTOR));
                    district.setInjectedPower(district.getInjectedPower() + powerReceived);
                    remainingDeficit -= powerReceived;

                    if (remainingDeficit <= 0) {
                        break;
                    }
                }
            }
        }

        return Math.max(0, remainingDeficit);
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

        // Prioritize sending to districts with deficits
        List<TransmissionLine> outgoingToDeficitDistricts = connectedLines.stream()
                .filter(line -> line.getFrom().equals(districtId) && line.getFlow() < line.getCapacity())
                .filter(line -> districts.get(line.getTo()).getEnergyDeficit() > 0)
                .toList();

        // If there are districts with deficits, prioritize them
        List<TransmissionLine> targetLines = !outgoingToDeficitDistricts.isEmpty()
                ? outgoingToDeficitDistricts
                : connectedLines.stream()
                .filter(line -> line.getFrom().equals(districtId) && line.getFlow() < line.getCapacity())
                .toList();

        if (targetLines.isEmpty()) {
            // Nowhere to send power, it becomes stranded
            district.setStrandedEnergy(district.getStrandedEnergy() + excessPower);
            return;
        }

        // Calculate distribution weights based on available capacity, resistance, and deficit priority
        double totalWeight = targetLines.stream()
                .mapToDouble(line -> {
                    double baseWeight = (line.getCapacity() - line.getFlow()) / line.getResistance();
                    // Give 3x weight to districts with deficits
                    return districts.get(line.getTo()).getEnergyDeficit() > 0 ? baseWeight * 3 : baseWeight;
                })
                .sum();

        if (totalWeight == 0) {
            district.setStrandedEnergy(district.getStrandedEnergy() + excessPower);
            return;
        }

        // Distribute power based on weights
        for (TransmissionLine line : targetLines) {
            District receivingDistrict = districts.get(line.getTo());
            double baseWeight = (line.getCapacity() - line.getFlow()) / line.getResistance();
            double lineWeight = (receivingDistrict.getEnergyDeficit() > 0 ? baseWeight * 3 : baseWeight) / totalWeight;
            double powerToSend = Math.min(
                    excessPower * lineWeight,
                    line.getCapacity() - line.getFlow()
            );

            if (powerToSend > 0) {
                line.setFlow(line.getFlow() + powerToSend);
                district.setExportedPower(district.getExportedPower() + powerToSend);

                // Add power to receiving district
                double powerReceived = powerToSend * (1 - (line.getResistance() * TRANSMISSION_LOSS_FACTOR));
                receivingDistrict.setInjectedPower(receivingDistrict.getInjectedPower() + powerReceived);

                // Update deficit if applicable
                if (receivingDistrict.getEnergyDeficit() > 0) {
                    double deficitReduction = Math.min(powerReceived, receivingDistrict.getEnergyDeficit());
                    receivingDistrict.setEnergyDeficit(receivingDistrict.getEnergyDeficit() - deficitReduction);

                    if (receivingDistrict.getEnergyConsumption() > 0) {
                        double actualPowerAvailable = receivingDistrict.getEnergyProduction() + receivingDistrict.getInjectedPower();
                        receivingDistrict.setDemandCoverage(Math.min(1.0, actualPowerAvailable / receivingDistrict.getEnergyConsumption()));
                    }
                }
            }
        }
    }

    /**
     * Calculate stress levels and apply monetary and popularity costs for all districts
     */
    private void calculateStressLevels() {
        // Calculate total system consumption for relative scaling
        double totalSystemConsumption = districts.values().stream()
                .mapToDouble(District::getEnergyConsumption)
                .sum();
        totalSystemConsumption = Math.max(totalSystemConsumption, 1.0); // Avoid division by zero

        // Calculate raw costs and track total monetary cost
        double totalMonetaryCost = 0.0;
        Map<String, Double> rawMonetaryCosts = new HashMap<>();

        for (District district : districts.values()) {
            // Calculate both surplus and deficit stress
            double surplusStress = calculateSurplusStress(district);
            double deficitStress = calculateDeficitStress(district);

            // Take the maximum since a district can't have both simultaneously
            double stressLevel = Math.max(surplusStress, deficitStress);
            district.setStressLevel(Math.min(1.0, stressLevel));

            // Set blackout condition
            district.setBlackout(stressLevel > 0.7 || district.getDemandCoverage() < 0.5);

            // Calculate monetary cost and popularity impact
            double consumptionRatio = district.getEnergyConsumption() / totalSystemConsumption;
            double monetaryCost = calculateMonetaryCost(district) * consumptionRatio;
            double popularityImpact = calculatePopularityImpact(district) * consumptionRatio;

            district.setMonetaryCost(monetaryCost);
            district.setPopularityImpact(popularityImpact);

            rawMonetaryCosts.put(district.getId().toString(), monetaryCost);
            totalMonetaryCost += monetaryCost;
        }

        // Apply global monetary cost cap (20% of starting gold = 1000)
        if (totalMonetaryCost > TOTAL_GOLD_CAP) {
            double scaleFactor = TOTAL_GOLD_CAP / totalMonetaryCost;
            for (District district : districts.values()) {
                double originalCost = rawMonetaryCosts.get(district.getId().toString());
                district.setMonetaryCost(originalCost * scaleFactor);
            }
        }
    }

    /**
     * Calculate stress from surplus energy
     */
    private double calculateSurplusStress(District district) {
        // Total power in the district: netProduction + injectedPower - exportedPower
        double totalPowerInDistrict = district.getNetProduction() + district.getInjectedPower() - district.getExportedPower();

        // Preserve any existing stranded energy from power distribution phase and local congestion
        double existingStrandedEnergy = district.getStrandedEnergy();

        // Check if district is overloaded (exceeding grid capacity)
        if (totalPowerInDistrict > district.getGridCapacity()) {
            // Additional stranded energy due to exceeding grid capacity
            double additionalStrandedEnergy = totalPowerInDistrict - district.getGridCapacity();
            district.setStrandedEnergy(existingStrandedEnergy + additionalStrandedEnergy);
        }

        double strandedEnergy = district.getStrandedEnergy();
        double stressLevel = 0.0;

        if (strandedEnergy > 0) {
            // Progressive stress calculation based on absolute stranded energy
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

        return stressLevel;
    }

    /**
     * Calculate stress from energy deficit
     */
    private double calculateDeficitStress(District district) {
        double deficit = district.getEnergyDeficit();
        double consumption = district.getEnergyConsumption();

        if (deficit <= 0 || consumption <= 0) return 0;

        // Calculate deficit ratio (0-1 scale, where 1 means no power at all)
        double deficitRatio = deficit / consumption;

        // Progressive stress curve - more aggressive than surplus
        // because deficits have immediate impact on users
        double stressLevel = 0.0;

        if (deficitRatio >= 0.5) {
            // 50%+ deficit = critical blackout conditions
            stressLevel = 0.9 + (deficitRatio - 0.5) * 0.2; // 0.9-1.0 range
        } else if (deficitRatio >= 0.3) {
            // 30-50% deficit = rolling blackouts needed
            stressLevel = 0.7 + (deficitRatio - 0.3) / 0.2 * 0.2; // 0.7-0.9 range
        } else if (deficitRatio >= 0.15) {
            // 15-30% deficit = brownout conditions
            stressLevel = 0.5 + (deficitRatio - 0.15) / 0.15 * 0.2; // 0.5-0.7 range
        } else if (deficitRatio >= 0.05) {
            // 5-15% deficit = voltage instability, equipment stress
            stressLevel = 0.3 + (deficitRatio - 0.05) / 0.1 * 0.2; // 0.3-0.5 range
        } else {
            // <5% deficit = manageable with load balancing
            stressLevel = deficitRatio / 0.05 * 0.3; // 0-0.3 range
        }

        return stressLevel;
    }

    /**
     * Calculate monetary cost from stress level
     * Now considers both surplus and deficit scenarios, scaled for game balance
     */
    private double calculateMonetaryCost(District district) {
        double stressLevel = district.getStressLevel();
        if (stressLevel <= 0) return 0;

        boolean hasDeficit = district.getEnergyDeficit() > 0;
        double costMultiplier = hasDeficit ? 1.5 : 1.0;

        if (district.isBlackout()) {
            // Complete blackout - high but manageable costs
            return BASE_MONETARY_COST * costMultiplier * (5 + stressLevel * 10);
        } else if (stressLevel > 0.5) {
            // Severe stress
            return BASE_MONETARY_COST * costMultiplier * (2 + stressLevel * 4);
        } else if (stressLevel > 0.3) {
            // Moderate stress
            return BASE_MONETARY_COST * costMultiplier * (1 + stressLevel * 3);
        } else if (stressLevel > 0.1) {
            // Minor stress
            return BASE_MONETARY_COST * costMultiplier * (0.5 + stressLevel * 2);
        } else {
            // Minimal stress
            return BASE_MONETARY_COST * costMultiplier * stressLevel;
        }
    }

    /**
     * Calculate popularity impact from stress level
     * Deficits have more severe political consequences, with small rewards for low stress
     */
    private double calculatePopularityImpact(District district) {
        double stressLevel = district.getStressLevel();

        boolean hasDeficit = district.getEnergyDeficit() > 0;
        double impactMultiplier = hasDeficit ? 1.5 : 1.0;

        if (stressLevel <= 0.3 && !hasDeficit) {
            // Reward for well-managed districts (no deficit, low stress)
            return BASE_POPULARITY_IMPACT * (0.1 + (0.3 - stressLevel) * 0.2); // Small positive boost
        } else if (stressLevel <= 0) {
            return 0;
        }

        if (district.isBlackout()) {
            // Complete blackout - severe but balanced consequences
            return impactMultiplier * (-5 - stressLevel * 10);
        } else if (stressLevel > 0.5) {
            // Severe conditions
            return impactMultiplier * (-2 - stressLevel * 4);
        } else if (stressLevel > 0.3) {
            // Noticeable disruption
            return impactMultiplier * (-1 - stressLevel * 3);
        } else if (stressLevel > 0.1) {
            // Moderate inconvenience
            return impactMultiplier * (-0.5 - stressLevel * 2);
        } else {
            // Minor complaints
            return impactMultiplier * (-stressLevel);
        }
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
            districtData.put("energyDeficit", district.getEnergyDeficit());
            districtData.put("demandCoverage", district.getDemandCoverage());
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
}
