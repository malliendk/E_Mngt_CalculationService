package com.dillian.e_mngt_backendforfrontend.services.DTObuilder;

import com.dillian.e_mngt_backendforfrontend.constants.ServerURLs;
import com.dillian.e_mngt_backendforfrontend.dtos.BuildingDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.BuildingRequestDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.InitiateDTO;
import com.dillian.e_mngt_backendforfrontend.dtos.SupervisorDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BuildingRetrieveService {

    private final RestClient restClient;

    public SupervisorDTO getSupervisorDTO(Long supervisorId) {
        String url = ServerURLs.SUPERVISOR_SERVICE_URL + "/" + supervisorId;
        ResponseEntity<SupervisorDTO> response = restClient
                .get()
                .uri(url)
                .retrieve()
                .toEntity(SupervisorDTO.class);
        SupervisorDTO supervisor = response.getBody();
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("GET Supervisor Service call successfully made");
        }
        if (supervisor != null) {
            log.info("Supervisor successfully retrieved: {}", supervisor);
        }
        return supervisor;
    }

    public List<BuildingDTO> getBuildingsById(InitiateDTO initiateDTO) {
        List<Long> ids = initiateDTO.getBuildingRequests().stream()
                .map(BuildingRequestDTO::getBuildingId)
                .toList();
        final ResponseEntity<List<BuildingDTO>> response = restClient
                .post()
                .uri(ServerURLs.BUILDING_SERVICE_URL + "/ids")
                .body(ids)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
        final List<BuildingDTO> uniqueBuildings = response.getBody();
        if (uniqueBuildings == null) {
            throw new RuntimeException();
        }
        Map<Long, BuildingDTO> buildingMap = uniqueBuildings.stream()
                .collect(Collectors.toMap(BuildingDTO::getId, Function.identity()));
        return ids.stream()
                .map(buildingMap::get)
                .toList();
    }
}
