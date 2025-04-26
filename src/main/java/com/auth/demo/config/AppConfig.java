package com.auth.demo.config;

import com.auth.demo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class AppConfig {

    /**
     * Bean definition for UserDetailsService.
     * This service is used by Spring Security to load user-specific data during authentication.
     * It fetches user details from the database using the UserRepository.
     *
     * @param userRepository The repository to fetch user data from the database.
     * @return A UserDetailsService implementation.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
            .map(user -> org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // Set the username (email) for authentication
                .password(user.getPassword()) // Set the encoded password
                .roles(user.getRole()) // Set the user's role(s)
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)); // Throw exception if user not found
    }

    /**
     * Bean definition for PasswordEncoder.
     * This encoder is used to hash passwords before saving them to the database
     * and to verify raw passwords during authentication.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password hashing
    }
}
