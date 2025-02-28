package com.dillian.e_mngt_backendforfrontend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Configuration
@Component
public class RestClientConfig {

    @Bean
    public RestClient RestClient() {
        return RestClient.builder().build();
    }
}