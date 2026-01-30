package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IncomeDTO {

    private double goldIncome;
    private double popularityIncome;
    private double researchIncome;
}
