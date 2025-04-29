package com.example.RankingApplication;

import com.example.RankingApplication.config.AuthConfig;
import com.example.RankingApplication.config.BcpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({BcpProperties.class, AuthConfig.class})
@EnableMongoRepositories(basePackages = "com.example.RankingApplication.repository")
@EntityScan(basePackages = "com.example.RankingApplication.model")
@EnableScheduling
public class GlickoRankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlickoRankingApplication.class, args);
	}

}
