package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.DTOStatsCalculationService;
import com.dillian.e_mngt_backendforfrontend.services.DTOBuilder.GameDTOBuilderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class DayTimeSchedulerService {

    private final WeatherService weatherService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final DTOStatsCalculationService statsCalculationService;

    public void scheduleWeatherType() {
        Runnable task = () -> {
            WeatherType newWeatherType = weatherService.getRandomWeatherType();
            log.info("Next weather type is sent: {}", newWeatherType);
        };
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }

    public void scheduleTimeOfDay() {
        Runnable task = () -> {
            TimeOfDay newTimeOfDay = weatherService.cycleThroughTimesOfDay();
            log.info("New type of day is sent: {}", newTimeOfDay);

        };
        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
    }
}
