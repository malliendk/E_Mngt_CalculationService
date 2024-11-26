package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.services.schedulers.ScheduledUpdateService;
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

    public void stopSchedulers() {
//        schedulerService
    }
}
