package com.alberto.rateLimiter.business;

import com.alberto.rateLimiter.model.Entity.User;
import com.alberto.rateLimiter.model.Roles.Role;
import com.alberto.rateLimiter.repository.RoleRepository;
import com.alberto.rateLimiter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    public User authenticate(String email, String password){

        User user = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("User Not found"));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("Invalid password");
        }
        return user;
    }

    public User register(String name, String email, String password){

       if (userRepository.findByEmail(email).isPresent()){
           throw new IllegalArgumentException("User already exists with this email");
       }

       Role userRole = roleRepository.findByName("ROLE_USER")
               .orElseThrow(()-> new RuntimeException("Default role not found"));

       User newUser = new User();
       newUser.setName(name);
       newUser.setEmail(email);
       newUser.setPassword(passwordEncoder.encode(password));
       newUser.getRoles().add(userRole);

       userRepository.save(newUser);
       log.debug("User registered: {}", email);

        return newUser;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
    }

    public User findById(Long id){
     return userRepository.findById(id)
             .orElseThrow(()-> new IllegalArgumentException("User not found with id: "+ id));
    }

    public void deleteUser(Long id){

        User user = findById(id);
        userRepository.delete(user);
        log.debug("User deleted: {}", id);
    }

}
