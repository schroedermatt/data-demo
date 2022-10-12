package org.msse.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DemoApplicationTest {
	@Test
	public void contextLoads() {
		assertThat(true).isTrue();
	}
}
