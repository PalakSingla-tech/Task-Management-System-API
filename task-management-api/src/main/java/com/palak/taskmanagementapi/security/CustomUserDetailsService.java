package com.palak.taskmanagementapi.security;

import com.palak.taskmanagementapi.entity.Admin;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.repository.AdminRepository;
import com.palak.taskmanagementapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Since AuthService passes email for login, try searching by email first
        // Check Admin first to avoid conflicts where the same email exists in both tables
        Optional<Admin> adminByEmail = adminRepository.findByEmail(identifier);
        if (adminByEmail.isPresent()) return adminByEmail.get();

        Optional<User> userByEmail = userRepository.findByEmail(identifier);
        if (userByEmail.isPresent()) return userByEmail.get();

        // JWT token validation passes the username, so we must also be able to search by username
        Optional<Admin> adminByUsername = adminRepository.findByUsername(identifier);
        if (adminByUsername.isPresent()) return adminByUsername.get();

        Optional<User> userByUsername = userRepository.findByUsername(identifier);
        if (userByUsername.isPresent()) return userByUsername.get();

        throw new UsernameNotFoundException("No user or admin found with identifier: " + identifier);
    }
}
