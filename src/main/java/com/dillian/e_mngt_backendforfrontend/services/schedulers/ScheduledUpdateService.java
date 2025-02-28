package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.services.DTObuilder.DayWeatherService;
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
        final ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
        Runnable task = () -> gameService.updateByTimeOfDay(extendedGameDTO);
        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
    }

    public void scheduleWeatherTypeUpdate() {
        final ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
        Runnable task = () -> gameService.updateByWeatherType(extendedGameDTO);
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }

    public void scheduleIncomeUpdate() {
        Runnable task = () -> {
            ExtendedGameDTO extendedGameDTO = gameService.getExtendedGameDTO();
            gameService.addIncome(extendedGameDTO);
            log.info("Next income is sent: {}", extendedGameDTO);
        };
        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
    }
}
