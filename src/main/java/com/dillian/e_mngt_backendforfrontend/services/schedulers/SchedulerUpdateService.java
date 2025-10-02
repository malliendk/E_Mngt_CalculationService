package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import com.dillian.e_mngt_backendforfrontend.utils.constants.SchedulerValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SchedulerUpdateService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final GameService gameService;

    private ScheduledFuture<?> timeOfDayTask;
    private ScheduledFuture<?> weatherTypeTask;
    private ScheduledFuture<?> incomeTask;
    private ScheduledFuture<?> popularityLossTask;

    public SchedulerUpdateService(final GameService gameService) {
        this.gameService = gameService;
    }

    public void startSchedulers() {
        scheduleTimeOfDayUpdate();
        scheduleWeatherTypeUpdate();
        scheduleIncomeUpdate();
    }

    public void startPopularityScheduler() {
        scheduleGoldPopularityIncomeLoss();
    }


    public void shutdownSchedulers() {
        if (timeOfDayTask != null && !timeOfDayTask.isCancelled()) {
            timeOfDayTask.cancel(true);
        }
        if (weatherTypeTask != null && !weatherTypeTask.isCancelled()) {
            weatherTypeTask.cancel(true);
        }
        if (incomeTask != null && !incomeTask.isCancelled()) {
            incomeTask.cancel(true);
        }
        if (popularityLossTask != null && !popularityLossTask.isCancelled()) {
            popularityLossTask.cancel(true);
        }
        log.info("All schedulers have been shut down.");
    }


    public void scheduleTimeOfDayUpdate() {
        timeOfDayTask = scheduler.scheduleAtFixedRate(() -> {
                    ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                    gameService.updateByTimeOfDay(extendedGameDTO);
                    log.info("Time of day update completed");
                }, 15,
                60,
                TimeUnit.SECONDS);
    }

    public void scheduleWeatherTypeUpdate() {
        weatherTypeTask = scheduler.scheduleAtFixedRate(() -> {
                    ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                    gameService.updateByWeatherType(extendedGameDTO);
                    log.info("Weather type update completed");
                }, 45,
                60,
                TimeUnit.SECONDS);
    }

    public void scheduleIncomeUpdate() {
        incomeTask = scheduler.scheduleAtFixedRate(() -> {
                    ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                    gameService.addIncome(extendedGameDTO);
                    log.info("Income update completed: {}", extendedGameDTO);
                }, SchedulerValues.INCOME_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.INCOME_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }

    public void scheduleGoldPopularityIncomeLoss() {
        popularityLossTask = scheduler.scheduleAtFixedRate(() -> {
            ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.subtractPopularityIncome(extendedGameDTO);
                log.info("Popularity loss update completed: {}", extendedGameDTO);
        }, 0,30, TimeUnit.SECONDS);
    }
}