package com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SupervisorDTO {

    private Long id;
    private String name;
    private String imageUrl;
    private String biography;
    private String classType;
    private String type;
    private String specialAbility;
    private int perkGoldIncome;
    private int perkGoldCost;
    private int perkPopularityIncome;
    private int perkPopularityCost;
    private int perkResearchIncome;
    private int perkResearchCost;
    private int perkEnvironmentalIncome;
    private int perkGridResilience;
    private int perkGridEfficiency;
    private int specialPerk;
}
