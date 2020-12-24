package com.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 
 * This calls basically launches the Application loading
 *  all auto/default configurations and scan the base packages
 *
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class BotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}

}
