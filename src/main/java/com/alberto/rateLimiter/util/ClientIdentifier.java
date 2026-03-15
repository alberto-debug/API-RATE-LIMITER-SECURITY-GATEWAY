package com.alberto.rateLimiter.util;


import com.alberto.rateLimiter.exception.TokenValidationException;
import com.alberto.rateLimiter.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientIdentifier {

    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenService tokenService;


    public String getSafeClientIdentifier(HttpServletRequest request){

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)){
                String token = authHeader.substring(7);
                if (!token.isEmpty()) {
                    return tokenService.validateToken(token);
                }
            }

            // Fallback: Use IP address for unauthenticated requests
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }
            log.debug("Using IP-based client identifier: {}", clientIp);
            return "ip:" + clientIp;

        } catch (Exception e) {
            log.warn("Failed to get client identifier, using remote IP as fallback");
            return "ip:" + request.getRemoteAddr();
        }
    }


    public String extractToken(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)){
            throw new TokenValidationException("Authorization Failed: Invalid or Missing Token");
        }

        String token = authHeader.substring(7);

        if (token.isEmpty()) {
            throw new TokenValidationException("Token is empty");
        }

        return token;
    }
}
