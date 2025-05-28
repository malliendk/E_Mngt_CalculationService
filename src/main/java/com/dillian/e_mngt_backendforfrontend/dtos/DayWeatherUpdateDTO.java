package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayWeatherUpdateDTO {

    private String timeOfDay;
    private String weatherType;
    private List<District> districts;
}
