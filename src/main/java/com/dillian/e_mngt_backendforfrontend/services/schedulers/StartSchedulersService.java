package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import com.dillian.e_mngt_backendforfrontend.dtos.MinimizedGameDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StartSchedulersService {

    private final ScheduledUpdateService schedulerService;

    public void startSchedulers(MinimizedGameDTO minimizedGameDTO) {
        schedulerService.scheduleTimeOfDayUpdate();
        schedulerService.scheduleWeatherTypeUpdate();
        schedulerService.scheduleIncomeUpdate();
        //startSSEService(minimizedGameDTO)
    }
}
