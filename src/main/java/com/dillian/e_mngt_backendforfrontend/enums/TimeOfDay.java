package com.dillian.e_mngt_backendforfrontend.enums;

import lombok.Getter;

@Getter
public enum TimeOfDay implements FactorProvider {

    MORNING("morning", 0.75, 0.75, 1.50),
    AFTERNOON("afternoon", 1, 1, 1),
    EVENING("evening", 1.50, 1.50, 0.50),
    NIGHT("night", 0, 0.25, 0.25);

    private final String name;
    private final double generationFactor;
    private final double housingConsumptionFactor;
    private final double industrialConsumptionFactor;

    TimeOfDay(final String name, double generationFactor, double housingConsumptionFactor, double industrialConsumptionFactor) {
        this.name = name;
        this.generationFactor = generationFactor;
        this.housingConsumptionFactor = housingConsumptionFactor;
        this.industrialConsumptionFactor = industrialConsumptionFactor;
    }

    @Override
    public double getGenerationFactor() {
        return generationFactor;
    }

    @Override
    public double getHousingConsumptionFactor() {
        return housingConsumptionFactor;
    }

    @Override
    public double getIndustrialConsumptionFactor() {
        return industrialConsumptionFactor;
    }
}
