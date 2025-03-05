package com.dillian.e_mngt_backendforfrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendForFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendForFrontendApplication.class, args);
	}

}
