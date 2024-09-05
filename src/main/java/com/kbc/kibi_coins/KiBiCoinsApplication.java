package com.kbc.kibi_coins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KiBiCoinsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiBiCoinsApplication.class, args);
	}

}
