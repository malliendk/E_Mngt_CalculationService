package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IncomeAddDTO {

    private double newFunds;
    private double newPopularity;
    private double newResearch;
}
