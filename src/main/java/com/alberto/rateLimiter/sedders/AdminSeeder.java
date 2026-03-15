package com.alberto.rateLimiter.sedders;

import com.alberto.rateLimiter.model.Entity.User;
import com.alberto.rateLimiter.model.Roles.Role;
import com.alberto.rateLimiter.repository.RoleRepository;
import com.alberto.rateLimiter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADM_EMAIL}")
    private String adminEmail;

    @Value("${ADM_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByEmail(adminEmail).isPresent()){
            log.info("Admin User already exists");
            return;
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(()-> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_ADMIN");
                    return roleRepository.save(newRole);
                });

        User admin = new User();
        admin.setName("SUPER ADMIN");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.getRoles().add(adminRole);

        userRepository.save(admin);
        log.info("Admin user created: {}", adminEmail);

    }
}
