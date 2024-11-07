package com.dillian.e_mngt_backendforfrontend.enums;

public enum TimeOfDay implements FactorProvider {

    MORNING(0.75, 0.75, 1.50),
    AFTERNOON(1, 1, 1),
    EVENING(1.50, 1.50, 0.50),
    NIGHT(0, 0.25, 0.25),;

    private final double generationFactor;
    private final double housingConsumptionFactor;
    private final double industrialConsumptionFactor;

    TimeOfDay(double generationFactor, double housingConsumptionFactor, double industrialConsumptionFactor) {
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
