package com.alberto.rateLimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest
class RateLimiterApplicationTests {


	@Container
	static PostgreSQLContainer postgres =
			new PostgreSQLContainer("postgres:15")
					.withDatabaseName("rateLimiter")
					.withUsername("test")
					.withPassword("test");

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {

		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

		registry.add("DB_NAME", () -> "rateLimiter");
		registry.add("DB_URL", postgres::getJdbcUrl);
		registry.add("DB_USERNAME", postgres::getUsername);
		registry.add("DB_PASSWORD", postgres::getPassword);

		registry.add("spring.data.redis.host", () -> "localhost");
		registry.add("spring.data.redis.port", () -> "6379");
		registry.add("spring.data.redis.password", () -> "RedisPass123");
		registry.add("REDIS_HOST", () -> "localhost");
		registry.add("REDIS_PORT", () -> "6379");
		registry.add("REDIS_PASSWORD", () -> "RedisPass123");

		registry.add("JWT_SECRET", () -> "test-JWT-secret");
		registry.add("KEY", () -> "test-JWT-secret");
		registry.add("JWT_EXPIRATION_MS", () -> "3600000");
		registry.add("JWT_ISSUER", () -> "test-issuer");

 		registry.add("ADM_EMAIL", () -> "admin@test.com");
		registry.add("ADM_PASSWORD", () -> "testadminpass");
	}


	@Test
	void contextLoads() {
	}

}
