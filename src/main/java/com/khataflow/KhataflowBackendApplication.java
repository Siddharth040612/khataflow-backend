package com.khataflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.khataflow.repository")
public class KhataflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KhataflowBackendApplication.class, args);
	}

}
