package com.dillian.e_mngt_backendforfrontend.enums;

/**
 * Provides factors for energy calculations.
 *
 * <p>Default implementations of consumption factors return zero,
 * indicating that the factor is not applicable. Implementing classes
 * should override these methods if consumption factors are relevant.
 */
public interface FactorProvider {

    /**
     * Returns the generation factor.
     *
     * @return the generation factor
     */
    double getGenerationFactor();

    /**
     * Returns the housing consumption factor.
     *
     * @return the housing consumption factor, or zero if not applicable
     */
    default double getHousingConsumptionFactor() {
        return 0;
    }

    /**
     * Returns the industrial consumption factor.
     *
     * @return the industrial consumption factor, or zero if not applicable
     */
    default double getIndustrialConsumptionFactor() {
        return 0;
    }
}
