package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
import com.dillian.e_mngt_backendforfrontend.enums.TimeOfDay;
import com.dillian.e_mngt_backendforfrontend.enums.WeatherType;
import com.dillian.e_mngt_backendforfrontend.services.DayWeatherService;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class ScheduledUpdateService {

    private final DayWeatherService dayWeatherService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final GameService gameService;

    public void scheduleTimeOfDayUpdate() {
        Runnable task = () -> {
            TimeOfDay newTimeOfDay = dayWeatherService.cycleThroughTimesOfDay();
            log.info("New type of day is sent: {}", newTimeOfDay);
            GameDTO gameDTO = gameService.getGameDTO();
            gameService.updateDtoByTimeOfDay(newTimeOfDay, gameDTO);
        };
        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
    }

    public void scheduleWeatherTypeUpdate() {
        Runnable task = () -> {
            WeatherType newWeatherType = dayWeatherService.getRandomWeatherType();
            log.info("Next weather type is sent: {}", newWeatherType);
            GameDTO gameDTO = gameService.getGameDTO();
            gameService.updateDtoByWeatherType(newWeatherType, gameDTO);
        };
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }

    public void scheduleIncomeUpdate() {
        Runnable task = () -> {
            GameDTO gameDTO = gameService.getGameDTO();
            gameService.addIncomeToDTO(gameDTO);
            log.info("Next income is sent: {}", gameDTO);
        };
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }
}
