package com.alberto.rateLimiter.business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    private static final int DEFAULT_WINDOW_SIZE = 60;

    @Override
    public boolean isAllowed(String clientId, int requestPerMinute, int windowSizeSeconds) {

        String key = "ratelimit:" + clientId;

        try {
            Long currentCount = redisTemplate.opsForValue().increment(key);

            if (currentCount ==1){
                redisTemplate.expire(key, Duration.ofSeconds(windowSizeSeconds));
            }

            return currentCount <= requestPerMinute;

        }catch (Exception e){
            log.error("Rate limiting error for client: {}", clientId);
            return true;
        }

    }

    @Override
    public boolean isAllowed(String clientId) {
        return isAllowed(clientId, DEFAULT_REQUESTS_PER_MINUTE, DEFAULT_WINDOW_SIZE);
    }


}
