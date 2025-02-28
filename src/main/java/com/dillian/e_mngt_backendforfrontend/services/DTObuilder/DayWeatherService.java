package com.dillian.e_mngt_backendforfrontend.services.DTObuilder;

import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
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

@Service
@Getter
@Slf4j
public class DayWeatherService {

    private final BuildingRetrieveService buildingRetrieveService;
    private List<TimeOfDay> timesOfDay;
    private List<WeatherType> weatherTypes;
    private WeatherType newWeatherType;
    private TimeOfDay newTimeOfDay;
    private int currentIndex = 0;

    public DayWeatherService(final BuildingRetrieveService buildingRetrieveService) {
        this.buildingRetrieveService = buildingRetrieveService;
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
        int totalEnergyProduction = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyProduction, buildings);
        int totalEnergyConsumption = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyConsumption, buildings);
        extendedGameDTO.setEnergyProduction((int)(totalEnergyProduction * timeOfDay.getGenerationFactor()));
        extendedGameDTO.setEnergyConsumption((int)((totalEnergyConsumption * timeOfDay.getHousingConsumptionFactor()
                        + totalEnergyConsumption * timeOfDay.getIndustrialConsumptionFactor())));
        extendedGameDTO.setTimeOfDay(timeOfDay.getName());
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
        int totalEnergyProduction = CalculationHelperService.sumBuildingProperty(
                BuildingDTO::getEnergyProduction, buildings);
        extendedGameDTO.setEnergyProduction((int)(totalEnergyProduction * newWeatherType.getGenerationFactor()));
        extendedGameDTO.setWeatherType(newWeatherType.getName());
        return extendedGameDTO;
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
