package com.dillian.e_mngt_backendforfrontend.services.DTObuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.FactorProvider;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.dillian.e_mngt_backendforfrontend.services.DTObuilder.CalculationHelperService.calculateGridLoad;

@Service
@Getter
@Slf4j
public class DayWeatherService {

    private final BuildingRetrieveService buildingRetrieveService;
    private final CalculationHelperService calculationHelperService;
    private List<TimeOfDay> timesOfDay;
    private List<WeatherType> weatherTypes;
    private WeatherType newWeatherType;
    private TimeOfDay newTimeOfDay;
    private int currentIndex = 0;

    public DayWeatherService(final BuildingRetrieveService buildingRetrieveService, final CalculationHelperService calculationHelperService) {
        this.buildingRetrieveService = buildingRetrieveService;
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
     *
//     * @param initiateDTO The DTO containing initialization parameters.
     * @param extendedGameDTO The game state to be updated.
     * @return The updated GameDTO with adjusted energy production and consumption.
     */
    public ExtendedGameDTO updateDTOByTimeOfDay(ExtendedGameDTO extendedGameDTO) {
        final List<BuildingDTO> buildings = extendedGameDTO.getBuildings();
        final TimeOfDay timeOfDay = cycleThroughTimesOfDay();
        extendedGameDTO.setTimeOfDay(timeOfDay.getName());
        int newEnergyProduction = calculateNewEnergyProduction(buildings, timeOfDay, extendedGameDTO);
        extendedGameDTO.setEnergyProduction(newEnergyProduction);
        int newEnergyConsumption = calculateNewEnergyConsumption(buildings, timeOfDay, extendedGameDTO);
        extendedGameDTO.setEnergyConsumption(newEnergyConsumption);
        double gridLoad = calculateGridLoad(newEnergyProduction, newEnergyConsumption, extendedGameDTO.getGridCapacity());
        extendedGameDTO.setGridLoad(gridLoad);
        return extendedGameDTO;
    }

    /**
     * Updates the GameDTO's energy production based on the current weather type.
     * <p>
     * The update is performed in-place by summing the BuildingDTOs' production and
     * consumption values within its stream.
     *
     * @param extendedGameDTO The game state to be updated.
     * @return The updated GameDTO with adjusted energy production based on weather conditions.
     */
    public ExtendedGameDTO updateDTOByWeatherType(ExtendedGameDTO extendedGameDTO) {
        final List<BuildingDTO> buildings = extendedGameDTO.getBuildings();
        WeatherType newWeatherType = getRandomWeatherType();
        extendedGameDTO.setWeatherType(newWeatherType.getName());
        int newEnergyProduction = calculateNewEnergyProduction(buildings, newWeatherType, extendedGameDTO);
        extendedGameDTO.setEnergyProduction(newEnergyProduction);
        double gridLoad = calculateGridLoad(newEnergyProduction, extendedGameDTO.getEnergyConsumption(), extendedGameDTO.getGridCapacity());
        extendedGameDTO.setGridLoad(gridLoad);
        return extendedGameDTO;
    }

    private int calculateNewEnergyProduction(List<BuildingDTO> buildings, FactorProvider factorProvider, ExtendedGameDTO extendedGameDTO) {
        int totalEnergyProduction = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyProduction, buildings);
        int powerPlantProduction = CalculationHelperService.sumPowerPlantProduction(buildings);
        int dayWeatherDependentProduction = totalEnergyProduction - powerPlantProduction;
        return (int)(dayWeatherDependentProduction * factorProvider.getGenerationFactor() +
                powerPlantProduction);
    }

    private int calculateNewEnergyConsumption(List<BuildingDTO> buildings, FactorProvider factorProvider, ExtendedGameDTO extendedGameDTO) {
        int totalEnergyConsumption = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyConsumption, buildings);
        int newHousingConsumption = (int)(totalEnergyConsumption * factorProvider.getHousingConsumptionFactor());
        int newIndustrialConsumption = (int)(totalEnergyConsumption * factorProvider.getIndustrialConsumptionFactor());
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
