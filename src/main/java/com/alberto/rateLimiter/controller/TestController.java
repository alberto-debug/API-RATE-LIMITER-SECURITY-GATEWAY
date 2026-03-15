package com.alberto.rateLimiter.controller;

import com.alberto.rateLimiter.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test(){

        return ResponseEntity.ok("No rate limit on this endpoint");

    }

    @GetMapping("/limited")
    @RateLimit(requestsPerMinute = 10)
    public ResponseEntity<String> limited(){
        return ResponseEntity.ok("This endpoint is rate limited to 10 requests per minute");
    }

    @GetMapping("/strict")
    @RateLimit(requestsPerMinute = 5)
    public ResponseEntity<String> strict() {
        return ResponseEntity.ok("This endpoint is rate limited to 5 requests per minute");
    }

    @GetMapping("/default")
    @RateLimit
    public ResponseEntity<String> defaultt() {
        return ResponseEntity.ok("this endpoint has default configs , 100 requests in each 60 seconds");
    }

    //Questions
    // 1. if i only put @RateLimit , i dont need to set any request per minute right ?
    //2. are request a window default , yes but then why am i definning other values here in controller
    //3. basically the rate limiter will limiot each controller endpoint right ?
}
