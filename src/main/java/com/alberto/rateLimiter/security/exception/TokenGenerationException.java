package com.alberto.rateLimiter.security.exception;

public class TokenGenerationException extends RuntimeException {
    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause){
        super(message, cause);
    }
}
