package com.dobidan.bandeutseolap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


@SpringBootApplication
public class BandeutseolapApplication {

	private static final Logger logger = LoggerFactory.getLogger(BandeutseolapApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(BandeutseolapApplication.class, args);
	}

	@Bean
	CommandLineRunner testDatabaseConnection(DataSource dataSource) {
		return args -> {
			try {
				dataSource.getConnection().close();
				logger.info("데이터베이스 연결!");
			} catch (Exception e) {
				logger.error("데이터베이스 연결 실패: ", e);
			}
		};
	}

}
