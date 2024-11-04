package com.dillian.e_mngt_backendforfrontend.services;

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

    private List<TimeOfDay> timesOfDay;
    private List<WeatherType> weatherTypes;
    private WeatherType newWeatherType;
    private TimeOfDay newTimeOfDay;
    private int currentIndex = 0;

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

    public TimeOfDay cycleThroughTimesOfDay() {
        this.newTimeOfDay = this.timesOfDay.get(currentIndex);
        this.currentIndex++;
        log.info("time of day: {}", this.newTimeOfDay);
        log.info("new index: {}", this.currentIndex);
        return this.newTimeOfDay;
    }

    public WeatherType getRandomWeatherType() {
        int randomIndex = new Random().nextInt(0, 4);
        this.newWeatherType = this.weatherTypes.get(randomIndex);
        return this.newWeatherType;
    }
}
