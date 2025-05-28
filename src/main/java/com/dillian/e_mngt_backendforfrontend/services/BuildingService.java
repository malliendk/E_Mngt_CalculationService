package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.services.utils.constants.ServerURLs;
import com.dillian.e_mngt_backendforfrontend.dtos.*;
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
import java.util.function.Function;



@Service
@AllArgsConstructor
@Slf4j
public class BuildingService {

    private final RestClient restClient;

    public InitiateDTO assignTilesToDistricts(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcesseBuildings) {
        initiateDTO = assignBuildingsToTiles(initiateDTO, fullyProcesseBuildings);

        Map<Long, District> districtMap = initiateDTO.getDistricts().stream().collect(Collectors.toMap(District::getId, district -> district));

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

    private InitiateDTO assignBuildingsToTiles(InitiateDTO initiateDTO, List<BuildingDTO> fullyProcessedBuildings) {
        // Create a map for quick lookup of BuildingDTOs by id
        // If there are duplicates in the building list, we'll use the last one encountered
        log.info("input building: {}", fullyProcessedBuildings);
        Map<Long, BuildingDTO> buildingMap = new HashMap<>();
        for (BuildingDTO building : fullyProcessedBuildings) {
            buildingMap.put(building.getId(), building);
        }

        // Iterate through the tiles and assign the corresponding BuildingDTO
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
        return initiateDTO;
    }

    public List<BuildingDTO> getBuildingsById(InitiateDTO initiateDTO) {
        List<Long> ids = initiateDTO.getBuildingRequests().stream().map(BuildingRequestDTO::getBuildingId).toList();
        log.info("Get buildings by ids: {}", ids);
        final ResponseEntity<List<BuildingDTO>> response = restClient.post().uri(ServerURLs.BUILDING_SERVICE_URL + "/ids").body(ids).retrieve().toEntity(new ParameterizedTypeReference<>() {
        });
        final List<BuildingDTO> buildings = response.getBody();
        if (buildings == null) {
            throw new RuntimeException();
        }
        List<BuildingDTO> result = new ArrayList<>();
        for (Long id : ids) {
            buildings.stream().filter(buildingDTO -> buildingDTO.getId().equals(id)).findFirst().ifPresent(result::add);
        }
        mapBuildingRequestsToBuildings(initiateDTO.getBuildingRequests(), result);
        return result;
    }

    /**
     * Maps values from BuildingRequestDTO to corresponding BuildingDTO objects
     * @param buildingRequests List of building requests containing values to map
     * @param buildings List of building DTOs to apply the values to
     */
    private void mapBuildingRequestsToBuildings(List<BuildingRequestDTO> buildingRequests, List<BuildingDTO> buildings) {
        for (BuildingDTO building : buildings) {
            buildingRequests.stream()
                    .filter(request -> request.getBuildingId().equals(building.getId()))
                    .findFirst()
                    .ifPresent(request -> {
                        building.setSolarPanelAmount(request.getSolarPanelAmount());
                        building.setEnergyProduction(request.getEnergyProduction());
                        building.setGoldIncome(request.getGoldIncome());
                        building.setResearchIncome(request.getResearchIncome());
                        building.setEnvironmentalScore(request.getEnvironmentalScore());
                    });
        }
    }
}
