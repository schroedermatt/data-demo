package org.msse.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MockDataDaemon {
	public static void main(String[] args) {
		SpringApplication.run(MockDataDaemon.class, args);
	}
}
