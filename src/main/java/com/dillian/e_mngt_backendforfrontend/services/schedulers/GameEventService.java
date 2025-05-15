package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.services.utils.constants.SchedulerValues;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class GameEventService {

    private final GameService gameService;
    private final IncomeDayWeatherUpdateService incomeDayWeatherUpdateService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public GameEventService(GameService gameService, IncomeDayWeatherUpdateService incomeDayWeatherUpdateService) {
        this.gameService = gameService;
        this.incomeDayWeatherUpdateService = incomeDayWeatherUpdateService;

        // Register callback for when all updates complete

        // Also schedule periodic checks in case we miss a callback
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::sendGameUpdatesIfNeeded,
                SchedulerValues.EVENT_SCHEDULER_OFFSET_SECONDS,
                SchedulerValues.EVENT_SCHEDULER_INTERVAL_SECONDS,
                TimeUnit.SECONDS);
    }

    /**
     * Registers a new SSE emitter for a client connection
     * @return SseEmitter for the client
     */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Add onCompletion callback
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            log.info("SSE connection closed. Total active connections: {}", emitters.size());
        });

        // Add onTimeout callback
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
            log.info("SSE connection timed out. Total active connections: {}", emitters.size());
        });

        // Add error callback
        emitter.onError(ex -> {
            emitter.completeWithError(ex);
            emitters.remove(emitter);
            log.error("SSE error: {}", ex.getMessage());
        });

        // Send initial data to the client
        try {
            ExtendedGameDTO currentGameData = gameService.getExtendedGameDTO();
            if (currentGameData != null) {
                MinimizedGameDTO minimizedData = gameService.minimizeGameDTO(currentGameData);
                emitter.send(SseEmitter.event()
                        .name("game-update")
                        .data(minimizedData));
                log.info("Initial game data successfully sent to client: {}", minimizedData);
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        // Add to active emitters list
        emitters.add(emitter);
        log.info("New SSE connection established. Total active connections: {}", emitters.size());

        return emitter;
    }

    /**
     * Send game updates to all clients if no updates are in progress
     */
    private void sendGameUpdatesIfNeeded() {
        // Only send updates if no scheduled updates are in progress

    }

    /**
     * Sends game updates to all connected clients
     */
    private void sendGameUpdates() {
        if (emitters.isEmpty()) {
            return; // No clients connected, nothing to do
        }

        ExtendedGameDTO gameData = gameService.getExtendedGameDTO();
        if (gameData == null) {
            log.warn("No game data available to send");
            return;
        }

        // Convert to minimized version for transmission
        MinimizedGameDTO minimizedData = gameService.minimizeGameDTO(gameData);

        // Create list of dead emitters to be removed
        List<SseEmitter> deadEmitters = new ArrayList<>();

        // Send update to all clients
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("game-update")
                        .data(minimizedData));
                log.info("successfully sent gameDTO to client: {}", minimizedData);
            } catch (IOException e) {
                deadEmitters.add(emitter);
                log.error("Failed to send game update to client: {}", e.getMessage());
            }
        });

        // Remove dead emitters
        if (!deadEmitters.isEmpty()) {
            emitters.removeAll(deadEmitters);
            log.info("Removed {} dead connections. Total active connections: {}",
                    deadEmitters.size(), emitters.size());
        }

        log.debug("Game update sent to {} clients", emitters.size());
    }
}
