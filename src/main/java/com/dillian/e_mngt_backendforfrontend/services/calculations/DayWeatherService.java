package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.DayWeatherUpdateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.BuildingService;
import com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService;
import com.dillian.e_mngt_backendforfrontend.utils.constants.ProdCon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService.*;

@Service
@Getter
@Slf4j
public class DayWeatherService {

    private final BuildingService buildingService;
    private final DistrictStatsCalculationService districtStatsCalculationService;
    private final CalculationHelperService calculationHelperService;
    private List<TimeOfDay> timesOfDay;
    private List<WeatherType> weatherTypes;
    private WeatherType newWeatherType;
    private TimeOfDay newTimeOfDay;
    private int currentIndex = 0;

    /**
     * Constructor for DayWeatherService.
     *
     * @param buildingService                Service for building-related operations.
     * @param districtStatsCalculationService Service for calculating district statistics.
     * @param calculationHelperService       Utility service for calculations.
     */
    public DayWeatherService(final BuildingService buildingService,
                             final DistrictStatsCalculationService districtStatsCalculationService,
                             final CalculationHelperService calculationHelperService) {
        this.buildingService = buildingService;
        this.districtStatsCalculationService = districtStatsCalculationService;
        this.calculationHelperService = calculationHelperService;
    }

    /**
     * Initializes the list of times of day when the application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void loadTimesOfDay() {
        this.timesOfDay = List.of(
                TimeOfDay.MORNING,
                TimeOfDay.AFTERNOON,
                TimeOfDay.EVENING,
                TimeOfDay.NIGHT);
    }

    /**
     * Initializes the list of weather types when the application starts.
     */
    @EventListener(ApplicationStartedEvent.class)
    private void loadWeatherTypes() {
        this.weatherTypes = List.of(
                WeatherType.SUNNY,
                WeatherType.MODERATE,
                WeatherType.OVERCAST,
                WeatherType.RAINY);
    }

    /**
     * Generates a DayWeatherUpdateDTO based on the next time of day and a random weather type.
     * Also calculates new energy production and consumption for each district.
     *
     * @param gameDTO The current game state.
     * @return A DTO containing updated weather and energy data.
     */
    public DayWeatherUpdateDTO updateDTOByTimeOfDay(ExtendedGameDTO gameDTO) {
        final TimeOfDay timeOfDay = cycleThroughTimesOfDay();
        final WeatherType newWeatherType = getRandomWeatherType();
        districtStatsCalculationService.calculateCumulativeDistrictValues(gameDTO.getDistricts());
        DayWeatherUpdateDTO dto = new DayWeatherUpdateDTO();
        dto.setTimeOfDay(timeOfDay.getName());
        dto.setWeatherType(newWeatherType.getName());
        dto.setNewProductions(calculateNewDistrictProductions(gameDTO.getDistricts(), timeOfDay));
        dto.setNewConsumptions(calculateNewDistrictConsumptions(gameDTO.getDistricts(), timeOfDay));
        return dto;
    }

    /**
     * Generates a DayWeatherUpdateDTO based on a random weather type and the current time of day.
     * Only updates energy production.
     *
     * @param gameDTO The current game state.
     * @return A DTO containing updated weather and production data.
     */
    public DayWeatherUpdateDTO updateDTOByWeatherType(ExtendedGameDTO gameDTO) {
        final WeatherType newWeatherType = getRandomWeatherType();
        final String timeOfDay = gameDTO.getTimeOfDay();
        districtStatsCalculationService.calculateCumulativeDistrictValues(gameDTO.getDistricts());
        DayWeatherUpdateDTO dto = new DayWeatherUpdateDTO();
        dto.setTimeOfDay(timeOfDay);
        dto.setWeatherType(newWeatherType.getName());
        dto.setNewProductions(calculateNewDistrictProductions(gameDTO.getDistricts(), newWeatherType));
        dto.setNewConsumptions(Collections.emptyMap());
        return dto;
    }

    /**
     * Sets the production and consumption maps in the DayWeatherUpdateDTO
     * using the current game state.
     *
     * @param dayWeatherDTO The DTO to update.
     * @param gameDTO       The current game state.
     * @return The updated DayWeatherUpdateDTO.
     */
    public DayWeatherUpdateDTO setProductionAndConsumption(DayWeatherUpdateDTO dayWeatherDTO, ExtendedGameDTO gameDTO) {
        dayWeatherDTO.setNewProductions(buildingService.createEnergyFlowMap(gameDTO, ProdCon.PRODUCTION));
        dayWeatherDTO.setNewConsumptions(buildingService.createEnergyFlowMap(gameDTO, ProdCon.CONSUMPTION));
        return dayWeatherDTO;
    }

    /**
     * Calculates new energy production values for each district based on a factor provider.
     *
     * @param districts      List of districts to process.
     * @param factorProvider The factor provider (e.g., time of day or weather).
     * @return A map of district IDs to new production values.
     */
    public Map<Long, Integer> calculateNewDistrictProductions(List<District> districts, FactorProvider factorProvider) {
        Map<Long, Integer> productionMap = new HashMap<>();
        for (District district : districts) {
            List<BuildingDTO> buildings = getBuildingsFromTiles(district);
            int production = calculateNewEnergyProduction(buildings, factorProvider);
            productionMap.put(district.getId(), production);
        }
        return productionMap;
    }

    /**
     * Calculates new energy consumption values for each district based on a factor provider.
     *
     * @param districts      List of districts to process.
     * @param factorProvider The factor provider (e.g., time of day or weather).
     * @return A map of district IDs to new consumption values.
     */
    public Map<Long, Integer> calculateNewDistrictConsumptions(List<District> districts, FactorProvider factorProvider) {
        Map<Long, Integer> consumptionMap = new HashMap<>();
        for (District district : districts) {
            List<BuildingDTO> buildings = getBuildingsFromTiles(district);
            int consumption = calculateNewEnergyConsumption(buildings, factorProvider);
            consumptionMap.put(district.getId(), consumption);
        }
        return consumptionMap;
    }

    /**
     * Calculates new energy production for a list of buildings using a factor provider.
     *
     * @param districtBuildings List of buildings in a district.
     * @param factorProvider    The factor provider (e.g., time of day or weather).
     * @return The calculated energy production value.
     */
    private int calculateNewEnergyProduction(List<BuildingDTO> districtBuildings, FactorProvider factorProvider) {
        int totalEnergyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, districtBuildings);
        int powerPlantProduction = sumPowerPlantProduction(districtBuildings);
        int variableProduction = totalEnergyProduction - powerPlantProduction;
        return (int) (variableProduction * factorProvider.getGenerationFactor() + powerPlantProduction);
    }

    /**
     * Calculates new energy consumption for a list of buildings using a factor provider.
     *
     * @param districtBuildings List of buildings in a district.
     * @param factorProvider    The factor provider (e.g., time of day or weather).
     * @return The calculated energy consumption value.
     */
    private int calculateNewEnergyConsumption(List<BuildingDTO> districtBuildings, FactorProvider factorProvider) {
        int totalEnergyConsumption = sumBuildingProperty(BuildingDTO::getEnergyConsumption, districtBuildings);
        int newHousingConsumption = (int) (totalEnergyConsumption * factorProvider.getHousingConsumptionFactor());
        int newIndustrialConsumption = (int) (totalEnergyConsumption * factorProvider.getIndustrialConsumptionFactor());
        return newHousingConsumption + newIndustrialConsumption;
    }

    /**
     * Cycles through the list of times of day and returns the next one.
     *
     * @return The next TimeOfDay value.
     */
    private TimeOfDay cycleThroughTimesOfDay() {
        this.newTimeOfDay = this.timesOfDay.get(currentIndex);
        this.currentIndex++;
        if (this.currentIndex >= this.timesOfDay.size()) {
            this.currentIndex = 0;
        }
        log.info("time of day: {}", this.newTimeOfDay);
        return this.newTimeOfDay;
    }

    /**
     * Selects a random weather type from the available list.
     *
     * @return A randomly selected WeatherType.
     */
    private WeatherType getRandomWeatherType() {
        int randomIndex = new Random().nextInt(0, 4);
        this.newWeatherType = this.weatherTypes.get(randomIndex);
        log.info("new weather type: {}", this.newWeatherType);
        return this.newWeatherType;
    }
}