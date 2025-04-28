package com.example.GlickoRankingApplication;

import com.example.GlickoRankingApplication.config.BcpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(BcpProperties.class)
@EnableMongoRepositories(basePackages = "com.example.GlickoRankingApplication.repository")
@EntityScan(basePackages = "com.example.GlickoRankingApplication.model")
@EnableScheduling
public class GlickoRankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlickoRankingApplication.class, args);
	}

}
