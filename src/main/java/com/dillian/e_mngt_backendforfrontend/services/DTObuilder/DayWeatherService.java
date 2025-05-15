package com.dillian.e_mngt_backendforfrontend.services.DTObuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.*;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.BuildingService;
import com.dillian.e_mngt_backendforfrontend.services.DistrictStatsCalculationService;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.services.utils.CalculationHelperService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.dillian.e_mngt_backendforfrontend.services.utils.CalculationHelperService.getBuildingsFromTiles;

@Service
@Getter
@Slf4j
public class DayWeatherService {

    private final BuildingService buildingService;
    private final DistrictStatsCalculationService districtStatsCalculationService;
    private final CalculationHelperService calculationHelperService;
    private final GameService gameService;
    private List<TimeOfDay> timesOfDay;
    private List<WeatherType> weatherTypes;
    private WeatherType newWeatherType;
    private TimeOfDay newTimeOfDay;
    private int currentIndex = 0;

    public DayWeatherService(final BuildingService buildingService, final DistrictStatsCalculationService districtStatsCalculationService, final CalculationHelperService calculationHelperService, final GameService gameService) {
        this.buildingService = buildingService;
        this.districtStatsCalculationService = districtStatsCalculationService;
        this.calculationHelperService = calculationHelperService;
        this.gameService = gameService;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void loadTimesOfDay() {
        this.timesOfDay = List.of(
                TimeOfDay.MORNING,
                TimeOfDay.AFTERNOON,
                TimeOfDay.EVENING,
                TimeOfDay.NIGHT);
    }

    @EventListener(ApplicationStartedEvent.class)
    private void loadWeatherTypes() {
        this.weatherTypes = List.of(
                WeatherType.SUNNY,
                WeatherType.MODERATE,
                WeatherType.OVERCAST,
                WeatherType.RAINY);
    }

    /**
     * Updates the GameDTO's energy production and energy consumption values based on the time of day.
     * <p>
     * The update is performed in-place by directly summing the BuildingDTOs' production
     * and consumption values.
     * <p>
     * //     * @param initiateDTO The DTO containing initialization parameters.
     *
     * @param districts The game state to be updated.
     * @return The updated GameDTO with adjusted energy production and consumption.
     */
    public DayWeatherUpdateDTO updateDTOByTimeOfDay(List<District> districts) {
        DayWeatherUpdateDTO updateDTO = updateDTOByWeatherType(districts);
        final TimeOfDay timeOfDay = cycleThroughTimesOfDay();
        for (District district : updateDTO.getDistricts()) {
            final List<BuildingDTO> districtBuildings = getBuildingsFromTiles(district);
            int newEnergyProduction = calculateNewEnergyProduction(districtBuildings, timeOfDay);
            int newEnergyConsumption = calculateNewEnergyConsumption(districtBuildings, timeOfDay);
            district.setEnergyProduction(newEnergyProduction);
            district.setEnergyConsumption(newEnergyConsumption);
        }
        updateDTO.setTimeOfDay(timeOfDay.getName());
        districtStatsCalculationService.calculateCumulativeDistrictValues(districts);
        updateDTO.setDistricts(updateDTO.getDistricts());
        return updateDTO;
    }

    /**
     * Updates the GameDTO's energy production based on the current weather type.
     * <p>
     * The update is performed in-place by summing the BuildingDTOs' production and
     * consumption values within its stream.
     *
     * @param districts The game state to be updated.
     * @return The updated GameDTO with adjusted energy production based on weather conditions.
     */
    public DayWeatherUpdateDTO updateDTOByWeatherType(List<District> districts) {
        WeatherType newWeatherType = getRandomWeatherType();
        for (District district : districts) {
            final List<BuildingDTO> districtBuildings = getBuildingsFromTiles(district);
            int newEnergyProduction = calculateNewEnergyProduction(districtBuildings, newWeatherType);
            district.setEnergyProduction(newEnergyProduction);
        }
        DayWeatherUpdateDTO updateDTO = new DayWeatherUpdateDTO();
        updateDTO.setWeatherType(newWeatherType.getName());
        districtStatsCalculationService.processExcessBalance(districts);
        updateDTO.setDistricts(districts);
        return updateDTO;
    }

    private int calculateNewEnergyProduction(List<BuildingDTO> districtBuildings, FactorProvider factorProvider) {
        int totalEnergyProduction = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyProduction, districtBuildings);
        int powerPlantProduction = CalculationHelperService.sumPowerPlantProduction(districtBuildings);
        int variableProduction = totalEnergyProduction - powerPlantProduction;
        return (int) (variableProduction * factorProvider.getGenerationFactor() +
                powerPlantProduction);
    }

    private int calculateNewEnergyConsumption(List<BuildingDTO> districtBuildings, FactorProvider factorProvider) {
        int totalEnergyConsumption = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyConsumption, districtBuildings);
        int newHousingConsumption = (int) (totalEnergyConsumption * factorProvider.getHousingConsumptionFactor());
        int newIndustrialConsumption = (int) (totalEnergyConsumption * factorProvider.getIndustrialConsumptionFactor());
        return newHousingConsumption + newIndustrialConsumption;
    }

    private TimeOfDay cycleThroughTimesOfDay() {
        this.newTimeOfDay = this.timesOfDay.get(currentIndex);
        this.currentIndex++;
        log.info("time of day: {}", this.newTimeOfDay);
        return this.newTimeOfDay;
    }

    private WeatherType getRandomWeatherType() {
        int randomIndex = new Random().nextInt(0, 4);
        this.newWeatherType = this.weatherTypes.get(randomIndex);
        log.info("new weather type: {}", this.newWeatherType);
        return this.newWeatherType;
    }
}
