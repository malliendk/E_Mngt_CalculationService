package com.dillian.e_mngt_backendforfrontend.services.schedulers;
import com.dillian.e_mngt_backendforfrontend.dtos.DayWeatherUpdateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.IncomeAddDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.concurrent.*;


// SSE Service
@Service
@Slf4j
public class GameEventService {


    private final GameService gameService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);


    public GameEventService(GameService gameService) {
        this.gameService = gameService;
    }

    public SseEmitter createIncomeStream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        log.info("Creating new SSE income stream");

        emitter.onCompletion(() -> {
            log.info("SSE income connection completed");
        });

        emitter.onTimeout(() -> {
            log.warn("SSE income connection timed out");
            emitter.complete();
        });

        emitter.onError((ex) -> {
            log.error("SSE income connection error: {}", ex.getMessage());
            emitter.completeWithError(ex);
        });

        scheduler.schedule(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("connection")
                        .data("Connected to income stream")
                        .id("connection-" + System.currentTimeMillis()));

                log.debug("Income connection confirmation sent to client");
            } catch (IOException e) {
                log.error("Error sending income connection confirmation: {}", e.getMessage());
                emitter.completeWithError(e);
                return;
            }

            ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(() -> {
                try {
                    sendIncomeUpdate(emitter);
                } catch (Exception e) {
                    log.error("Error in scheduled income update: {}", e.getMessage());
                    emitter.completeWithError(e);
                }
            }, 1, 30, TimeUnit.SECONDS);

            emitter.onCompletion(() -> {
                scheduledTask.cancel(false);
                log.debug("Cancelled income scheduled task");
            });

            emitter.onTimeout(() -> {
                scheduledTask.cancel(false);
                log.debug("Cancelled income scheduled task due to timeout");
            });

        }, 31, TimeUnit.SECONDS);

        return emitter;
    }

    public SseEmitter createDayWeatherStream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        log.info("Creating new SSE weather stream");

        emitter.onCompletion(() -> {
            log.info("SSE weather connection completed");
        });


        emitter.onTimeout(() -> {
            log.warn("SSE weather connection timed out");
            emitter.complete();
        });


        emitter.onError((ex) -> {
            log.error("SSE weather connection error: {}", ex.getMessage());
            emitter.completeWithError(ex);
        });

        scheduler.schedule(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("connection")
                        .data("Connected to weather stream")
                        .id("connection-" + System.currentTimeMillis()));


                log.debug("Weather connection confirmation sent to client");
            } catch (IOException e) {
                log.error("Error sending weather connection confirmation: {}", e.getMessage());
                emitter.completeWithError(e);
                return;
            }

            ScheduledFuture<?> scheduledTask = scheduler.scheduleAtFixedRate(() -> {
                try {
                    sendWeatherUpdate(emitter);
                } catch (Exception e) {
                    log.error("Error in scheduled weather update: {}", e.getMessage());
                    emitter.completeWithError(e);
                }
            }, 16, 30, TimeUnit.SECONDS);

            emitter.onCompletion(() -> {
                scheduledTask.cancel(false);
                log.debug("Cancelled weather scheduled task");
            });

            emitter.onTimeout(() -> {
                scheduledTask.cancel(false);
                log.debug("Cancelled weather scheduled task due to timeout");
            });

        }, 1, TimeUnit.SECONDS);

        return emitter;
    }

    private void sendIncomeUpdate(SseEmitter emitter) throws IOException {
        IncomeAddDTO incomeDTO = gameService.getIncomeAddDTO();
        if (incomeDTO != null) {
            emitter.send(SseEmitter.event()
                    .name("income-update")
                    .data(incomeDTO)
                    .id("income-" + System.currentTimeMillis()));
            log.debug("Income update sent: {}", incomeDTO);
        } else {
            log.warn("No income data available");
        }
    }

    private void sendWeatherUpdate(SseEmitter emitter) throws IOException {
        DayWeatherUpdateDTO weatherDTO = gameService.getDayWeatherUpdateDTO();
        if (weatherDTO != null) {
            emitter.send(SseEmitter.event()
                    .name("weather-update")
                    .data(weatherDTO)
                    .id("weather-" + System.currentTimeMillis()));
            log.debug("Weather update sent: {}", weatherDTO);
        } else {
            log.warn("No weather data available");
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

