package com.auth.demo.controller;

import com.auth.demo.entity.User;
import com.auth.demo.repository.UserRepository;
import com.auth.demo.service.UserService; // Ensure this import matches the package of UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
       
            user.setRole("USER"); // Assign default role
        
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).build(); // Forbidden for non-admins
        }

        return ResponseEntity.ok(userRepository.findAll());
    }

    /**
     * Endpoint to delete a user by ID (Admin Only).
     *
     * @param userId The ID of the user to delete.
     * @param authentication The authenticated admin user.
     * @return A success message.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body("Access denied. Only admins can delete users.");
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    /**
     * Endpoint to update the role of a user (Admin Only).
     *
     * @param userId The ID of the user whose role is to be updated.
     * @param request A map containing the new role.
     * @param authentication The authenticated admin user.
     * @return A success message.
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.status(403).body("Access denied. Only admins can update user roles.");
        }

        String newRole = request.get("role");
        userService.updateUserRole(userId, newRole);
        return ResponseEntity.ok("User role updated successfully.");
    }
}