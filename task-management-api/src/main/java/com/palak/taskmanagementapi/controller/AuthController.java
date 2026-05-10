package com.palak.taskmanagementapi.controller;

import com.palak.taskmanagementapi.dto.*;
import com.palak.taskmanagementapi.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> userLogin(@RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("Received login request for email: {}", loginRequestDTO.getEmail());
        return ResponseEntity.ok(authService.userLogin(loginRequestDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> userSignup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("Received signup request for username: {}", signUpRequestDTO.getUsername());
        return ResponseEntity.ok(authService.signup(signUpRequestDTO));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginResponseDTO> adminLogin(@RequestBody AdminLoginRequestDTO adminLoginRequestDTO) {
        log.info("Received admin login request for email: {}", adminLoginRequestDTO.getEmail());
        return ResponseEntity.ok(authService.adminLogin(adminLoginRequestDTO));
    }

    @PostMapping("/admin/signup")
    public ResponseEntity<SignUpResponseDTO> adminSignup(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("Received admin signup request for username: {}", signUpRequestDTO.getUsername());
        return ResponseEntity.ok(authService.adminSignup(signUpRequestDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refresh(@RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO) {
        log.info("Received token refresh request");
        return ResponseEntity.ok(authService.refresh(tokenRefreshRequestDTO));
    }
}
