package com.alberto.rateLimiter.controller;

import com.alberto.rateLimiter.DTOs.LoginRequestDTO;
import com.alberto.rateLimiter.DTOs.RegisterRequestDTO;
import com.alberto.rateLimiter.DTOs.ResponseDTO;
import com.alberto.rateLimiter.annotation.RateLimit;
import com.alberto.rateLimiter.business.UserService;
import com.alberto.rateLimiter.model.Entity.User;
import com.alberto.rateLimiter.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    @RateLimit(requestsPerMinute = 5)
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO body){

        try {

            User user = userService.authenticate(body.email(), body.password());
            String token = tokenService.generateToken(user);

            ResponseDTO response = new ResponseDTO("User logged successfully", token);
            log.debug("User Logged in: {}", body.email());

            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){

            log.warn("Login failed for email: {}", body.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO("Invalid email or password", null));

        }catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("An error occurred during login", null));
        }

    }

    @PostMapping("/register")
    @RateLimit(requestsPerMinute = 5)
    public ResponseEntity<ResponseDTO> register(@Valid  @RequestBody RegisterRequestDTO body){

        try {

            log.debug("Registration attempt for email: {}", body.email());

            User user = userService.register(body.name(), body.email(), body.password());
            String token = tokenService.generateToken(user);

            ResponseDTO response = new ResponseDTO("User registered successfully", token);
            log.debug("User registered: {}", body.email());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        }catch (IllegalArgumentException e){

            log.warn("registration failed: {}", e.getMessage());
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(e.getMessage(), null));

        } catch (Exception e){

            log.error("Unexpected error during registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("An error occurred during registration", null));
        }

    }
}