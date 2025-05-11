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
    private double energyProduction;
    private double energyConsumption;
    private int gridCapacity;
    private double gridLoad;
    private List<Tile> tiles;
}