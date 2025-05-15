package com.dillian.e_mngt_backendforfrontend.services;

import com.dillian.e_mngt_backendforfrontend.services.utils.constants.ServerURLs;
import com.dillian.e_mngt_backendforfrontend.dtos.SupervisorDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
@Slf4j
public class SupervisorService {

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
}
