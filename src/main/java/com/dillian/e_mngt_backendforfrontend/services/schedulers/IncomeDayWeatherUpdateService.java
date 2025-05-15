package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.services.utils.constants.SchedulerValues;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
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
public class IncomeDayWeatherUpdateService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final GameService gameService;

    public void initSchedulers() {
        scheduleTimeOfDayUpdate();
        scheduleWeatherTypeUpdate();
        scheduleIncomeUpdate();
    }

    /**
     * Schedule time of day update every 60 seconds
     */
    public void scheduleTimeOfDayUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
                ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.updateByTimeOfDay(extendedGameDTO);
                log.info("Time of day update completed");
        }, SchedulerValues.DAYTIME_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.DAYTIME_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Schedule weather type update every 30 seconds
     */
    public void scheduleWeatherTypeUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
                ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.updateByWeatherType(extendedGameDTO);
                log.info("Weather type update completed");
        }, SchedulerValues.WEATHER_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.WEATHER_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Schedule income update every 60 seconds
     */
    public void scheduleIncomeUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
                ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.addIncome(extendedGameDTO);
                log.info("Income update completed: {}", extendedGameDTO);
        }, SchedulerValues.INCOME_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.INCOME_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }
}