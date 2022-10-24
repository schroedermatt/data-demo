package org.msse.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({
		// loaded from kafka module
		@PropertySource("classpath:application-kafka.yaml"),
		// loaded from postgres module
		@PropertySource("classpath:application-postgres.yaml")
})
@SpringBootApplication
public class MockDataApplication {
	public static void main(String[] args) {
		SpringApplication.run(MockDataApplication.class, args);
	}

	@Profile("postgres")
	@Configuration
	@EnableAutoConfiguration(exclude = {
			RedisAutoConfiguration.class,
			RedisRepositoriesAutoConfiguration.class
	})
	public class PostgresProfileConfiguration {}

	@Profile("kafka")
	@Configuration
	@EnableAutoConfiguration(exclude = {
			DataSourceAutoConfiguration.class,
			DataSourceTransactionManagerAutoConfiguration.class,
			HibernateJpaAutoConfiguration.class,
			RedisRepositoriesAutoConfiguration.class
	})
	@EnableConfigurationProperties(value = { KafkaConfig.Cluster.class, KafkaConfig.Topics.class })
	public class KafkaProfileConfiguration {}
}
