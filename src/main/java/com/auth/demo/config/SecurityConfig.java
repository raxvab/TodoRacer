package com.auth.demo.config;

import com.auth.demo.security.JwtAuthenticationEntryPoint;
import com.auth.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application.
 * Configures authentication, authorization, and JWT filters.
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint entryPoint, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.entryPoint = entryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Bean for AuthenticationManager.
     * Used to authenticate users during login.
     *
     * @param configuration Spring Security's authentication configuration.
     * @return AuthenticationManager instance.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Security filter chain configuration.
     * Defines which endpoints are secured and how JWT tokens are validated.
     *
     * @param http HttpSecurity object for configuring security.
     * @return SecurityFilterChain instance.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .cors(cors -> cors.disable()) // Disable CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/logout", "/users/register").permitAll() // Public endpoints
                        .requestMatchers("/tasks/**").authenticated() // All task-related endpoints require authentication
                    //    .requestMatchers("/auth/refresh").permitAll() // Public endpoint for token refresh
                        .requestMatchers("/users/**").hasRole("ADMIN") // Only admins can access user-related endpoints
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint)) // Handle unauthorized access
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Stateless session management

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }
}