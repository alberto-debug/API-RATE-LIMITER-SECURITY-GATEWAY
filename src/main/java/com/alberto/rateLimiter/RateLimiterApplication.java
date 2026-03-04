package com.alberto.rateLimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListenerMethodProcessor;

@SpringBootApplication
public class RateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void Home(){
		System.out.println("Server Running at: http://localhost:8080");
	}

}
