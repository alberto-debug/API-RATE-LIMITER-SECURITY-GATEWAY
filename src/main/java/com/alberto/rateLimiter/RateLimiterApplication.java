package com.alberto.rateLimiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class RateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("Server Running at: http://localhost:8080");
	}
}