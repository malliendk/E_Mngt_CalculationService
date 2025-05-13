package com.dillian.e_mngt_backendforfrontend.dtos;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class District {

    private Long id;
    private int energyProduction;
    private int energyConsumption;
    private int excessBalance;
    private int gridCapacity;
    private double gridLoad;
    private List<Integer> incomingExcessBalances;
    private List<Tile> tiles;
}