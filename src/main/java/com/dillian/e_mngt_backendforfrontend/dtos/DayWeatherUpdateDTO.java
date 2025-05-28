package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class DayWeatherUpdateDTO {

    private final String timeOfDay;
    private final String weatherType;
    private final List<District> districts;
}
