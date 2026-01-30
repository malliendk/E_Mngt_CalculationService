package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.ExtendedGameDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.IncomeDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IncomeUpdateService {

    public IncomeDTO createIncomeDTO(ExtendedGameDTO gameDTO) {
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setGoldIncome(gameDTO.getGoldIncome());
        incomeDTO.setPopularityIncome(gameDTO.getPopularityIncome());
        incomeDTO.setResearchIncome(gameDTO.getResearchIncome());
        return incomeDTO;
    }

    public ExtendedGameDTO addIncomeToGameDTO(IncomeDTO incomeDTO, ExtendedGameDTO gameDTO) {
        gameDTO.setFunds(gameDTO.getFunds() + incomeDTO.getGoldIncome());
        gameDTO.setPopularity(gameDTO.getPopularity() + incomeDTO.getPopularityIncome());
        gameDTO.setResearch(gameDTO.getResearch() + incomeDTO.getResearchIncome());
        return gameDTO;
    }
}
