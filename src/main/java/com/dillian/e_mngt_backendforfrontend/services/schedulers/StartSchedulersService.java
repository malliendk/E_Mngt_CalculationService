package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class StartSchedulersService {

    private final IncomeDayWeatherUpdateService incomeDayWeatherUpdateService;

    public void startSchedulers(MinimizedGameDTO minimizedGameDTO) {
        log.info("Starting schedulers with game data: {}", minimizedGameDTO);
        incomeDayWeatherUpdateService.initSchedulers();
    }
}
