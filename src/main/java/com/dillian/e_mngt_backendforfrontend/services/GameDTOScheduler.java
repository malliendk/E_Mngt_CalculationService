package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.GameDTO;
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
public class GameDTOScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final GameDTOBuilderService gameDTOBuilderService;

    public void scheduleIncome(GameDTO gameDTO) {
        Runnable task = () -> {
            gameDTOBuilderService.addIncome(gameDTO);
            log.info("income updated");
        };
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }

    public void scheduleGameDTO(GameDTO gameDTO) {
        Runnable task = () -> {
//            gameDTOBuilderService.
        };
        scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }
}
