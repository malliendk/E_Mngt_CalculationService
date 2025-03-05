package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.constants.SchedulerValues;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ScheduledUpdateService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private final GameService gameService;

    // Track active update tasks
    private final AtomicInteger activeUpdateCount = new AtomicInteger(0);
    private final AtomicBoolean updateInProgress = new AtomicBoolean(false);

    // Callback for when all updates are complete
    @Setter
    private Runnable onAllUpdatesCompleteCallback;

    public ScheduledUpdateService(GameService gameService) {
        this.gameService = gameService;
    }


    /**
     * Check if updates are currently in progress
     * @return true if updates are in progress, false otherwise
     */
    public boolean isUpdateInProgress() {
        return updateInProgress.get();
    }

    /**
     * Initialize and start all schedulers.
     * Schedules three periodic updates:
     * - Time of day update (every 60 seconds)
     * - Weather type update (every 30 seconds)
     * - Income update (every 60 seconds)
     *
     * Also sets up a completion checker that runs every 30 seconds, with an initial
     * delay of 30.5 seconds to give time for the first updates to complete.
     */
    public void initSchedulers() {
        scheduleTimeOfDayUpdate();
        scheduleWeatherTypeUpdate();
        scheduleIncomeUpdate();

        scheduler.scheduleAtFixedRate(
                this::checkAndNotifyUpdatesComplete,
                SchedulerValues.EVENT_SCHEDULER_OFFSET_SECONDS,
                SchedulerValues.EVENT_SCHEDULER_INTERVAL_SECONDS,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Schedule time of day update every 60 seconds
     */
    public void scheduleTimeOfDayUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                startUpdate();
                ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.updateByTimeOfDay(extendedGameDTO);
                log.info("Time of day update completed");
            } finally {
                completeUpdate();
            }
        }, SchedulerValues.BASIC_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.DAYTIME_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Schedule weather type update every 30 seconds
     */
    public void scheduleWeatherTypeUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                startUpdate();
                ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.updateByWeatherType(extendedGameDTO);
                log.info("Weather type update completed");
            } finally {
                completeUpdate();
            }
        }, SchedulerValues.BASIC_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.WEATHER_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Schedule income update every 60 seconds
     */
    public void scheduleIncomeUpdate() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                startUpdate();
                ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
                gameService.addIncome(extendedGameDTO);
                log.info("Income update completed: {}", extendedGameDTO);
            } finally {
                completeUpdate();
            }
        }, SchedulerValues.BASIC_SCHEDULER_INITIAL_DELAY,
                SchedulerValues.INCOME_UPDATE_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Start an update and increment the active count
     */
    private void startUpdate() {
        activeUpdateCount.incrementAndGet();
        updateInProgress.set(true);
    }

    /**
     * Complete an update, decrement the active count, and check if all updates are complete
     */
    private void completeUpdate() {
        int remainingUpdates = activeUpdateCount.decrementAndGet();
        if (remainingUpdates <= 0) {
            updateInProgress.set(false);
            activeUpdateCount.set(0); // Reset to ensure we don't go negative
            notifyUpdateComplete();
        }
    }

    /**
     * Check if all updates are complete and notify if needed
     */
    private void checkAndNotifyUpdatesComplete() {
        if (updateInProgress.get() && activeUpdateCount.get() <= 0) {
            updateInProgress.set(false);
            notifyUpdateComplete();
        }
    }

    /**
     * Notify that updates are complete using the registered callback
     */
    private void notifyUpdateComplete() {
        if (onAllUpdatesCompleteCallback != null) {
            try {
                onAllUpdatesCompleteCallback.run();
            } catch (Exception e) {
                log.error("Error executing update complete callback", e);
            }
        }
    }
}