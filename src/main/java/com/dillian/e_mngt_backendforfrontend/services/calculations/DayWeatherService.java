package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.DayWeatherUpdateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.District;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.BuildingService;
import com.dillian.e_mngt_backendforfrontend.utils.CalculationHelperService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

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

    public DayWeatherService(final BuildingService buildingService, final DistrictStatsCalculationService districtStatsCalculationService, final CalculationHelperService calculationHelperService) {
        this.buildingService = buildingService;
        this.districtStatsCalculationService = districtStatsCalculationService;
        this.calculationHelperService = calculationHelperService;
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
     * @param gameDTO The game state to be updated.
     * @return The updated GameDTO with adjusted energy production and consumption.
     */
    public DayWeatherUpdateDTO updateDTOByTimeOfDay(ExtendedGameDTO gameDTO) {
        final TimeOfDay timeOfDay = cycleThroughTimesOfDay();
        final WeatherType randomWeatherType = getRandomWeatherType();
        for (District district : gameDTO.getDistricts()) {
            final List<BuildingDTO> districtBuildings = getBuildingsFromTiles(district);
            int newEnergyProduction = calculateNewEnergyProduction(districtBuildings, timeOfDay);
            int newEnergyConsumption = calculateNewEnergyConsumption(districtBuildings, timeOfDay);
            district.setEnergyProduction(newEnergyProduction);
            district.setEnergyConsumption(newEnergyConsumption);
        }
        districtStatsCalculationService.calculateCumulativeDistrictValues(gameDTO.getDistricts());
        DayWeatherUpdateDTO updateDayWeatherDTO = new DayWeatherUpdateDTO();
        updateDayWeatherDTO.setWeatherType(newWeatherType.getName());
        updateDayWeatherDTO.setTimeOfDay(timeOfDay.getName());
        updateDayWeatherDTO.setDistricts(gameDTO.getDistricts());
        return updateDayWeatherDTO;
    }

    /**
     * Updates the GameDTO's energy production based on the current weather type.
     * <p>
     * The update is performed in-place by summing the BuildingDTOs' production and
     * consumption values within its stream.
     *
     * @param gameDTO The game state to be updated.
     * @return The updated GameDTO with adjusted energy production based on weather conditions.
     */
    public DayWeatherUpdateDTO updateDTOByWeatherType(ExtendedGameDTO gameDTO) {
        WeatherType newWeatherType = getRandomWeatherType();
        String timeOfDay = gameDTO.getTimeOfDay();
        for (District district : gameDTO.getDistricts()) {
            final List<BuildingDTO> districtBuildings = getBuildingsFromTiles(district);
            int newEnergyProduction = calculateNewEnergyProduction(districtBuildings, newWeatherType);
            district.setEnergyProduction(newEnergyProduction);
        }
        districtStatsCalculationService.calculateCumulativeDistrictValues(gameDTO.getDistricts());

        DayWeatherUpdateDTO updateDayWeatherDTO = new DayWeatherUpdateDTO();
        updateDayWeatherDTO.setWeatherType(newWeatherType.getName());
        updateDayWeatherDTO.setTimeOfDay(timeOfDay);
        updateDayWeatherDTO.setDistricts(gameDTO.getDistricts());
        return updateDayWeatherDTO;
    }

    private int calculateNewEnergyProduction(List<BuildingDTO> districtBuildings, FactorProvider factorProvider) {
        int totalEnergyProduction = sumBuildingProperty(BuildingDTO::getEnergyProduction, districtBuildings);
        int powerPlantProduction = sumPowerPlantProduction(districtBuildings);
        int variableProduction = totalEnergyProduction - powerPlantProduction;
        return (int) (variableProduction * factorProvider.getGenerationFactor() + powerPlantProduction);
    }

    private int calculateNewEnergyConsumption(List<BuildingDTO> districtBuildings, FactorProvider factorProvider) {
        int totalEnergyConsumption = sumBuildingProperty(
                BuildingDTO::getEnergyConsumption, districtBuildings);
        int newHousingConsumption = (int) (totalEnergyConsumption * factorProvider.getHousingConsumptionFactor());
        int newIndustrialConsumption = (int) (totalEnergyConsumption * factorProvider.getIndustrialConsumptionFactor());
        return newHousingConsumption + newIndustrialConsumption;
    }

    private TimeOfDay cycleThroughTimesOfDay() {
        this.newTimeOfDay = this.timesOfDay.get(currentIndex);
        this.currentIndex++;
        if (this.currentIndex >= this.timesOfDay.size()) {
            this.currentIndex = 0;
        }
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
