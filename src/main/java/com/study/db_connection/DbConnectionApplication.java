package com.study.db_connection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DbConnectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbConnectionApplication.class, args);
	}

}
