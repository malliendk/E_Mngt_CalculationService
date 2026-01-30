package com.dillian.e_mngt_backendforfrontend.services.calculations;

import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IncomeLossCalculator {

    static double lossFactor = 0.1;

    public ExtendedGameDTO calculateLossByDistrictStressLevel(ExtendedGameDTO gameDTO) {
        double totalGoldLoss = gameDTO.getDistricts().stream()
                .mapToDouble(District::getMonetaryCost)
                .sum();
        double totalPopularityLoss = gameDTO.getDistricts().stream()
                .mapToDouble(District::getPopularityImpact)
                .sum();
        gameDTO.setGoldIncome(gameDTO.getGoldIncome() - totalGoldLoss);
        gameDTO.setPopularityIncome(gameDTO.getPopularityIncome() - totalPopularityLoss);
        return gameDTO;
    }

    public double calculateNewGoldIncome(List<District> districts) {
        double goldIncomeLoss = districts.stream()
                .mapToDouble(District::getMonetaryCost)
                .sum();
        double cumulativeGoldIncome = districts.stream()
                .mapToDouble(District::getGoldIncome)
                .sum();
        return cumulativeGoldIncome - goldIncomeLoss;
    }

    public double calculateNewPopularityIncome(List<District> districts) {
        double popularityIncomeLoss = districts.stream()
                .mapToDouble(District::getPopularityImpact)
                .sum();
        double cumulativePopularityIncome = districts.stream()
                .mapToDouble(District::getPopularityIncome)
                .sum();
        return cumulativePopularityIncome - popularityIncomeLoss;
    }
}