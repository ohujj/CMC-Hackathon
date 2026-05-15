package com.cmchackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CmchackathonApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmchackathonApplication.class, args);
	}

}
