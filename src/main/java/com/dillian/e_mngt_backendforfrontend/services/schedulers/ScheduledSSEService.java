package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.services.GameService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@AllArgsConstructor
public class ScheduledSSEService {

    private final GameService gameService;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

//    public void post() {
//        Runnable task = () -> {
//            GameDTO gameDTO = gameService.getGameDto();
//        }
//    }
}
