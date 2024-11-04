package com.dillian.e_mngt_backendforfrontend.services.schedulers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SchedulerService {

    private final ScheduledUpdateService schedulerService;

    public void startSchedulers() {
        schedulerService.scheduleTimeOfDayUpdate();
        schedulerService.scheduleWeatherTypeUpdate();
        schedulerService.scheduleIncomeUpdate();
    }
}
