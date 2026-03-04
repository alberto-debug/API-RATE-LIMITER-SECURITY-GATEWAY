package com.alberto.rateLimiter.model.Entity;


import com.alberto.rateLimiter.model.Roles.Role;
import com.alberto.rateLimiter.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class RoleSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByname("ROLE_USER").isEmpty()){
            roleRepository.save(new Role(null, "ROLE_USER", new HashSet<>()));
        }

        if (roleRepository.findByname("ROLE_ADMIN").isEmpty()){
            roleRepository.save(new Role(null, "ROLE_ADMIN", new HashSet<>()));
        }
    }
}
