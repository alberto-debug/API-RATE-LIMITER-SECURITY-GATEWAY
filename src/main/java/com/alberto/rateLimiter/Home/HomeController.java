package com.alberto.rateLimiter.Home;

import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> HomePage(){
        Map<String, Object> response = new HashMap<>();
        response.put("name", "API Rate Limiter & Security Gateway");
        response.put("version", "1.0.0");

        return response;
    }
}
