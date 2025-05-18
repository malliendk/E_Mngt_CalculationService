package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.dtos.DayWeatherUpdateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.IncomeAddDTO;
import com.dillian.e_mngt_backendforfrontend.services.GameService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@AllArgsConstructor
public class GameEventService {

    private final GameService gameService;
    private final List<SseEmitter> incomeEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> weatherEmitters = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public SseEmitter subscribeToIncome() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        setupEmitter(emitter, incomeEmitters);
        return emitter;
    }

    public SseEmitter subscribeToWeather() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        setupEmitter(emitter, weatherEmitters);
        return emitter;
    }

    private void setupEmitter(SseEmitter emitter, List<SseEmitter> emitterList) {
        emitterList.add(emitter);
        emitter.onCompletion(() -> emitterList.remove(emitter));
        emitter.onTimeout(() -> emitterList.remove(emitter));
        emitter.onError(e -> emitterList.remove(emitter));
    }

    private void startSchedulers() {
        scheduler.scheduleAtFixedRate(this::sendIncomeUpdate, 61, 60, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::sendWeatherUpdate, 1, 30, TimeUnit.SECONDS);
    }

    private void sendIncomeUpdate() {
        IncomeAddDTO dto = gameService.getIncomeAddDTO();
        sendToEmitters(incomeEmitters, "income-update", dto);
    }

    private void sendWeatherUpdate() {
        DayWeatherUpdateDTO dto = gameService.getDayWeatherUpdateDTO();
        sendToEmitters(weatherEmitters, "day-weather-update", dto);
    }

    private void sendToEmitters(List<SseEmitter> emitters, String eventName, Object dto) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(dto));
            } catch (IOException e) {
                emitter.completeWithError(e);
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }
}
