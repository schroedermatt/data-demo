package org.msse.demo;

import org.msse.demo.config.InitialLoadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(value = { InitialLoadProperties.class })
@PropertySources({
		// loaded from postgres module
		@PropertySource("classpath:application-postgres.yaml")
})
public class MockDataDaemon {
	public static void main(String[] args) {
		SpringApplication.run(MockDataDaemon.class, args);
	}
}
