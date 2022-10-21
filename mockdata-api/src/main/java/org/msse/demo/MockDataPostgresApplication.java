package org.msse.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import javax.annotation.PostConstruct;

@PropertySources({
		// loaded from postgres module
		@PropertySource("classpath:application-postgres.yaml"),
})
@SpringBootApplication(exclude = {
		KafkaAutoConfiguration.class
})
public class MockDataPostgresApplication {
	public static void main(String[] args) {
		SpringApplication.run(MockDataPostgresApplication.class, args);
	}
}
