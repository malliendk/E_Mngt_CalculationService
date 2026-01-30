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

    /**
     * Constructor for SchedulerUpdateService.
     *
     * @param gameService The service providing game-related operations.
     */
    public SchedulerUpdateService(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Starts the main game update schedulers:
     * - Time of day updates
     * - Weather type updates
     * - Income updates
     */
    public void startSchedulers() {
        scheduleTimeOfDayUpdate();
        scheduleWeatherTypeUpdate();
        scheduleIncomeUpdate();
    }

    /**
     * Starts the scheduler responsible for periodically reducing popularity income.
     */
    public void startPopularityScheduler() {
        scheduleGoldPopularityIncomeLoss();
    }

    /**
     * Shuts down all active schedulers if they are running.
     */
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

    /**
     * Schedules periodic updates to the time of day in the game.
     * Runs every 60 seconds after an initial delay of 15 seconds.
     */
    public void scheduleTimeOfDayUpdate() {
        timeOfDayTask = scheduler.scheduleAtFixedRate(() -> {
            ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
            gameService.updateByTimeOfDay(extendedGameDTO);
            log.info("Time of day update completed");
        }, SchedulerValues.INCOME_SCHEDULER_INITIAL_DELAY, SchedulerValues.DAYTIME_UPDATE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Schedules periodic updates to the weather type in the game.
     * Runs every 60 seconds after an initial delay of 45 seconds.
     */
    public void scheduleWeatherTypeUpdate() {
        weatherTypeTask = scheduler.scheduleAtFixedRate(() -> {
            ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
            gameService.updateByWeatherType(extendedGameDTO);
            log.info("Weather type update completed");
        }, SchedulerValues.WEATHER_SCHEDULER_INITIAL_DELAY, SchedulerValues.WEATHER_UPDATE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Schedules periodic income updates in the game.
     * Uses configured initial delay and interval from {@link SchedulerValues}.
     */
    public void scheduleIncomeUpdate() {
        incomeTask = scheduler.scheduleAtFixedRate(
                gameService::addIncome,
                SchedulerValues.INCOME_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.INCOME_UPDATE_SECONDS,
                TimeUnit.SECONDS
        );
    }

    /**
     * Schedules periodic reduction of popularity income and gold income.
     * Runs every 30 seconds with no initial delay.
     */
    public void scheduleGoldPopularityIncomeLoss() {
        popularityLossTask = scheduler.scheduleAtFixedRate(() -> {
            ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
            gameService.subtractPopularityIncome(extendedGameDTO);
            log.info("Popularity loss update completed: {}", extendedGameDTO);
        }, 0, 30, TimeUnit.SECONDS);
    }
}