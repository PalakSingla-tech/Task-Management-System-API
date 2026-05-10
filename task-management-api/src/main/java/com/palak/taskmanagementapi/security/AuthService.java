package com.palak.taskmanagementapi.security;

import com.palak.taskmanagementapi.dto.*;
import com.palak.taskmanagementapi.exception.ResourceNotFoundException;
import com.palak.taskmanagementapi.entity.Admin;
import com.palak.taskmanagementapi.entity.RefreshToken;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.repository.AdminRepository;
import com.palak.taskmanagementapi.repository.RefreshTokenRepository;
import com.palak.taskmanagementapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO userLogin(LoginRequestDTO loginRequestDTO) {
        log.info("User login attempt for: {}", loginRequestDTO.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        String refreshToken = generateAndSaveRefreshToken(user.getUsername(), user.getRole());

        return new LoginResponseDTO(token, refreshToken, user.getUserId());
    }

    public SignUpResponseDTO signup(SignUpRequestDTO signupRequestDTO) {
        if (userRepository.findByUsername(signupRequestDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User already exists with username " + signupRequestDTO.getUsername());
        }
        if (userRepository.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with email " + signupRequestDTO.getEmail());
        }

        User user = User.builder()
                .username(signupRequestDTO.getUsername())
                .email(signupRequestDTO.getEmail())
                .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
                .role("USER")
                .build();
        
        user = userRepository.save(user);

        return new SignUpResponseDTO(user.getUserId(), user.getUsername());
    }

    public SignUpResponseDTO adminSignup(SignUpRequestDTO signupRequestDTO) {
        if (adminRepository.findByUsername(signupRequestDTO.getUsername()).isPresent())
            throw new IllegalArgumentException("Admin already exists with username " + signupRequestDTO.getUsername());
        
        if (adminRepository.findByEmail(signupRequestDTO.getEmail()).isPresent())
            throw new IllegalArgumentException("Admin already exists with email " + signupRequestDTO.getEmail());

        Admin admin = Admin.builder()
                .email(signupRequestDTO.getEmail())
                .username(signupRequestDTO.getUsername())
                .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
                .role("ADMIN")
                .build();
        
        admin = adminRepository.save(admin);

        return new SignUpResponseDTO(admin.getAdminId(), admin.getUsername());
    }

    public AdminLoginResponseDTO adminLogin(AdminLoginRequestDTO adminLoginRequestDTO) {
        log.info("Admin login attempt for: {}", adminLoginRequestDTO.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        adminLoginRequestDTO.getEmail(),
                        adminLoginRequestDTO.getPassword()));

        Admin admin = (Admin) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(admin);
        String refreshToken = generateAndSaveRefreshToken(admin.getUsername(), "ADMIN");

        return new AdminLoginResponseDTO(token, refreshToken, admin.getAdminId());
    }

    public TokenRefreshResponseDTO refresh(TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(token -> {
                    if (token.getExpiryDate().before(new Date())) {
                        refreshTokenRepository.delete(token);
                        throw new RuntimeException("Refresh token was expired. Please make a new signin request");
                    }

                    String username = token.getUsername();
                    String accessToken;
                    
                    if ("ADMIN".equals(token.getRole())) {
                        Admin admin = adminRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with username: " + username));
                        accessToken = authUtil.generateAccessToken(admin);
                    } else {
                        User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
                        accessToken = authUtil.generateAccessToken(user);
                    }

                    return new TokenRefreshResponseDTO(accessToken);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token is not in database!"));
    }

    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    protected String generateAndSaveRefreshToken(String username, String role) {
        // Clear existing tokens for this user
        refreshTokenRepository.deleteByUsername(username);
        
        String token = authUtil.generateRefreshToken(username);
        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(token)
                .role(role)
                .expiryDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }
}
