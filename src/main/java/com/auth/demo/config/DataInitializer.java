package com.auth.demo.config;

import com.auth.demo.entity.User;
import com.auth.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes the database with default users (Admin and Regular User).
 * Runs at application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create an admin user if it doesn't exist
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole("ADMIN"); // Explicitly assign the ADMIN role
            userRepository.save(adminUser);
        }

        // Create a regular user if it doesn't exist
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User regularUser = new User();
            regularUser.setEmail("user@example.com");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setRole("USER"); // Assign role directly as a string
            userRepository.save(regularUser);
        }
    }
}