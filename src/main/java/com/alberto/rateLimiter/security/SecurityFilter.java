package com.alberto.rateLimiter.security;

import com.alberto.rateLimiter.model.Entity.User;
import com.alberto.rateLimiter.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);
        String email = null;

        if (token!=null){
            email = tokenService.validateToken(token);
        }

        if (email !=null){

            User user = userRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));

            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            var authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("User authenticated: {}", email);
        }

        filterChain.doFilter(request, response);

    }

    private String recoverToken(HttpServletRequest request){

       String authHeader = request.getHeader("Authorization");

       if (authHeader==null || !authHeader.startsWith("Bearer ")){
           return null;
       }

        return authHeader.substring(7);
    }
}
