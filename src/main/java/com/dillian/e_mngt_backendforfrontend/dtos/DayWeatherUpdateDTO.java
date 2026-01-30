package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayWeatherUpdateDTO {

    private String timeOfDay;
    private String weatherType;
    private Map<Long, Integer> newProductions;
    private Map<Long, Integer> newConsumptions;
}
