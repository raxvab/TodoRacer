// package com.auth.demo.service;

// import com.auth.demo.entity.User;
// import com.auth.demo.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// @Service
// public class CustomUserDetailsService implements UserDetailsService {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//         // Fetch user from the database
//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

//         // Build and return UserDetails object
//         return org.springframework.security.core.userdetails.User.builder()
//                 .username(user.getEmail())
//                 .password(user.getPassword())
//                 .roles(user.getRole()) // Use the role string directly
//                 .accountLocked(!user.isAccountNonLocked())
//                 .disabled(!user.isEnabled()) // need to perform a check for arch type
//                 .build();
//     }
// }