package org.msse.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
		// loaded from postgres module
		@PropertySource("classpath:application-postgres.yaml")
})
public class MockDataApplication {
	public static void main(String[] args) {
		SpringApplication.run(MockDataApplication.class, args);
	}
}
