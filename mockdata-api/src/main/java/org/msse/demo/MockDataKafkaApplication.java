package org.msse.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({
		// loaded from kafka module
		@PropertySource("classpath:application-kafka.yaml")
})
@SpringBootApplication(
		exclude = {
			DataSourceAutoConfiguration.class,
			DataSourceTransactionManagerAutoConfiguration.class,
			HibernateJpaAutoConfiguration.class
		}
)
@EnableConfigurationProperties(value = { KafkaConfig.Cluster.class, KafkaConfig.Topics.class })
public class MockDataKafkaApplication {
	public static void main(String[] args) {
		SpringApplication.run(MockDataKafkaApplication.class, args);
	}
}
