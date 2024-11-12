package com.dillian.e_mngt_backendforfrontend.enums;

import lombok.Getter;

@Getter
public enum WeatherType implements FactorProvider {

    SUNNY(1.5),
    MODERATE(1),
    OVERCAST(0.5),
    RAINY(0.25);

    private final double generationFactor;

    WeatherType(double generationFactor) {
        this.generationFactor = generationFactor;
    }

    @Override
    public double getGenerationFactor() {
        return generationFactor;
    }
}

