package com.alberto.rateLimiter.security;


import com.alberto.rateLimiter.model.Entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${KEY}")
    private String secret;

    public String generateToken(User user){

        Algorithm algorithm = Algorithm.HMAC256(secret);

        String token  = JWT.create()
                .withIssuer("login-auth-api")
                .withSubject(user.getEmail())
                .withExpiresAt(GenerateExpirationDate())
                .sign(algorithm);

        return token;
    }

    public String validateToken(String token){
        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

        }catch (JWTVerificationException jwtVerificationException){

            return "Error validating the token";
        }
    }

    private Instant GenerateExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-3:00"));
    }

}
