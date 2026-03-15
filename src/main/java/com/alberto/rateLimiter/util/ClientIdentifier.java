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

    public String getUserIdentifier(HttpServletRequest request){

        String token = extractToken(request);

        return tokenService.validateToken(token);
    }


    public String extractToken(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)){
            throw new TokenValidationException("Authorization FailedL: Invalid or Missing Token");
        }

        String token = authHeader.substring(7);

        if (token.isEmpty()) {
            throw new TokenValidationException("Token is empty");
        }

        return token;
    }
}
