package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DayWeatherUpdateDTO {

    private String timeOfDay;
    private String weatherType;
    private List<District> districts;
}
