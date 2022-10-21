package org.msse.demo;

import org.msse.demo.config.InitialLoadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@PropertySources({
		// loaded from kafka module
		@PropertySource("classpath:application-kafka.yaml"),
		// loaded from postgres module
		@PropertySource("classpath:application-postgres.yaml")
})
@EnableConfigurationProperties(value = {InitialLoadProperties.class })
@SpringBootApplication
public class MockDataDaemon {
	public static void main(String[] args) {
		SpringApplication.run(MockDataDaemon.class, args);
	}

	@Profile("kafka")
	@EnableAutoConfiguration(exclude = {
			DataSourceAutoConfiguration.class,
			DataSourceTransactionManagerAutoConfiguration.class,
			HibernateJpaAutoConfiguration.class
	})
	@EnableCaching
	@Configuration
	@EnableConfigurationProperties(value = { KafkaConfig.Cluster.class, KafkaConfig.Topics.class })
	public class KafkaProfileConfiguration {}
}