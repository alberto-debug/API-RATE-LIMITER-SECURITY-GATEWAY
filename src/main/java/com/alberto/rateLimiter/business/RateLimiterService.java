package com.alberto.rateLimiter.business;

public interface RateLimiterService {

    boolean isAllowed(String clientId, int requestPerMinute, int windowSizeSeconds);
    boolean isAllowed(String clientId);
}
