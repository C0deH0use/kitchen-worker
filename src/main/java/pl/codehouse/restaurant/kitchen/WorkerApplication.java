package pl.codehouse.restaurant.kitchen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Kitchen Worker Spring Boot Starter class.
 */
@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class WorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
	}

}
