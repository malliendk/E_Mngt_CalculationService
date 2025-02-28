package com.dillian.e_mngt_backendforfrontend.enums;

import lombok.Getter;

@Getter
public enum WeatherType implements FactorProvider {

    SUNNY("sunny", 1.5),
    MODERATE("moderate", 1),
    OVERCAST("overcast", 0.5),
    RAINY("rainy", 0.25);

    private final String name;
    private final double generationFactor;

    WeatherType(final String name, double generationFactor) {
        this.name = name;
        this.generationFactor = generationFactor;
    }

    @Override
    public double getGenerationFactor() {
        return generationFactor;
    }
}

