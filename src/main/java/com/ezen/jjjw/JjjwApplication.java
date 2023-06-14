package com.ezen.jjjw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JjjwApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjjwApplication.class, args);
	}

}
