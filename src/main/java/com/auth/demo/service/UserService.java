package com.auth.demo.service;

import com.auth.demo.entity.User;
import com.auth.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with default role as "USER".
     * 
     * @param user The user to be registered.
     */
    public void registerUser(User user) {
        saveUser(user);
    }

    /**
     * Saves a user to the database.
     * 
     * @param user The user to be saved.
     */
    public void saveUser(User user) {
        // Assign default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        userRepository.save(user);
    }

    /**
     * Fetches a user by email.
     * 
     * @param email The email of the user.
     * @return The user if found.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Fetches all users from the database.
     * 
     * @return List of all users.
     */
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
 /**
     * Deletes a user by ID.
     *
     * @param userId The ID of the user to delete.
     */
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    /**
     * Updates the role of a user.
     *
     * @param userId The ID of the user whose role is to be updated.
     * @param newRole The new role to assign to the user.
     */
    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setRole(newRole);
        userRepository.save(user);
    }
    
}