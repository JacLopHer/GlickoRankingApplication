package com.example.GlickoRankingApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.GlickoRankingApplication.repository")
@EntityScan(basePackages = "com.example.GlickoRankingApplication.model")
public class GlickoRankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlickoRankingApplication.class, args);
	}

}
