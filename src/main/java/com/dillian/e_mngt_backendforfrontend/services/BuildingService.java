package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.dtos.*;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.District;
import com.dillian.e_mngt_backendforfrontend.dtos.inGameObjects.Tile;
import com.dillian.e_mngt_backendforfrontend.utils.constants.ProdCon;
import com.dillian.e_mngt_backendforfrontend.utils.constants.ServerURLs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BuildingService {

    private final RestClient restClient;

    /**
     * Assigns tiles to their corresponding districts based on district IDs.
     * Also assigns buildings to tiles.
     *
     * @param initiateDTO           The DTO containing initial game setup data.
     * @param fullyProcesseBuildings List of buildings that have been fully processed.
     * @return The updated InitiateDTO with tiles assigned to districts.
     */
    public InitiateDTO assignTilesToDistricts(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcesseBuildings) {
        assignBuildingsToTiles(initiateDTO, fullyProcesseBuildings);
        Map<Long, District> districtMap = initiateDTO.getDistricts().stream()
                .collect(Collectors.toMap(District::getId, district -> district));
        for (District district : initiateDTO.getDistricts()) {
            district.setTiles(new ArrayList<>());
        }
        for (Tile tile : initiateDTO.getTiles()) {
            Long districtId = tile.getDistrictId();
            if (districtId != null) {
                District district = districtMap.get(districtId);
                if (district != null) {
                    district.getTiles().add(tile);
                }
            }
        }
        return initiateDTO;
    }

    /**
     * Assigns buildings to tiles based on building IDs.
     *
     * @param initiateDTO           The DTO containing initial game setup data.
     * @param fullyProcessedBuildings List of buildings that have been fully processed.
     */
    private void assignBuildingsToTiles(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        log.info("input building: {}", fullyProcessedBuildings);
        Map<Long, BuildingDTO> buildingMap = new HashMap<>();
        for (BuildingDTO building : fullyProcessedBuildings) {
            buildingMap.put(building.getId(), building);
        }

        for (Tile tile : initiateDTO.getTiles()) {
            Long buildingId = tile.getBuildingId();
            if (buildingId != null) {
                BuildingDTO building = buildingMap.get(buildingId);
                if (building != null) {
                    tile.setBuilding(building);
                } else {
                    System.out.println("Warning: No building found with ID: " + buildingId);
                }
            }
        }
        log.info("processed tiles for initiateDTO: {}", initiateDTO.getTiles());
    }

    /**
     * Retrieves buildings from the backend service based on building request IDs.
     * Also maps request-specific values to the retrieved buildings.
     *
     * @param initiateDTO The DTO containing building requests.
     * @return A list of BuildingDTOs corresponding to the requested IDs.
     */
    public List<BuildingDTO> getBuildingsById(InitiateDTO initiateDTO) {
        List<Long> ids = initiateDTO.getBuildingRequests().stream()
                .map(BuildingRequestDTO::getBuildingId)
                .toList();
        log.info("Get buildings by ids: {}", ids);
        final ResponseEntity<List<BuildingDTO>> response = restClient.post()
                .uri(ServerURLs.BUILDING_SERVICE_URL + "/ids")
                .body(ids)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        final List<BuildingDTO> buildings = response.getBody();
        if (buildings == null) {
            throw new RuntimeException();
        }
        List<BuildingDTO> result = new ArrayList<>();
        for (Long id : ids) {
            buildings.stream()
                    .filter(buildingDTO -> buildingDTO.getId().equals(id))
                    .findFirst()
                    .ifPresent(result::add);
        }
        mapBuildingRequestsToBuildings(initiateDTO.getBuildingRequests(), result);
        return result;
    }

    /**
     * Maps values from BuildingRequestDTO to corresponding BuildingDTO objects.
     *
     * @param buildingRequests List of building requests containing values to map.
     * @param buildings        List of building DTOs to apply the values to.
     */
    private void mapBuildingRequestsToBuildings(List<BuildingRequestDTO> buildingRequests, List<BuildingDTO> buildings) {
        for (BuildingDTO buildingDTO : buildings) {
            buildingRequests.stream()
                    .filter(request -> request.getBuildingId().equals(buildingDTO.getId()))
                    .findFirst()
                    .ifPresent(request -> {
                        buildingDTO.setSolarPanelAmount(request.getSolarPanelAmount());
                        buildingDTO.setEnergyProduction(request.getEnergyProduction());
                        buildingDTO.setPopularityIncome(request.getPopularityIncome());
                        buildingDTO.setGoldIncome(request.getGoldIncome());
                        buildingDTO.setResearchIncome(request.getResearchIncome());
                        buildingDTO.setEnvironmentalScore(request.getEnvironmentalScore());
                    });
        }
    }

    /**
     * Creates a map of district IDs to either energy production or consumption values,
     * depending on the specified flow direction.
     *
     * @param gameDTO       The game state containing district data.
     * @param flowDirection Either "PRODUCTION" or "CONSUMPTION".
     * @return A map of district IDs to energy values.
     */
    public Map<Long, Integer> createEnergyFlowMap(ExtendedGameDTO gameDTO, String flowDirection) {
        return gameDTO.getDistricts().stream()
                .collect(Collectors.toMap(
                        District::getId,
                        district -> ProdCon.PRODUCTION.equals(flowDirection)
                                ? district.getEnergyProduction()
                                : district.getEnergyConsumption()
                ));
    }

    /**
     * Updates the energy production and consumption values of districts in the gameDTO
     * based on the values provided in the DayWeatherUpdateDTO.
     *
     * @param dayWeatherUpdateDTO DTO containing new production and consumption values.
     * @param gameDTO             The current game state to be updated.
     * @return The updated gameDTO with new energy values applied to districts.
     */
    public ExtendedGameDTO mapEnergyUpdates(DayWeatherUpdateDTO dayWeatherUpdateDTO, ExtendedGameDTO gameDTO) {
        Map<Long, Integer> newProductions = dayWeatherUpdateDTO.getNewProductions();
        Map<Long, Integer> newConsumptions = dayWeatherUpdateDTO.getNewConsumptions();
        gameDTO.getDistricts().forEach(district -> {
            Long districtId = district.getId();
            Integer production = newProductions.get(districtId);
            if (production != null) {
                district.setEnergyProduction(production);
            }
            Integer consumption = newConsumptions.get(districtId);
            if (consumption != null) {
                district.setEnergyConsumption(consumption);
            }
        });
        return gameDTO;
    }
}