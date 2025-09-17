package com.vinylshop.service;

import com.vinylshop.dto.AuthResponse;
import com.vinylshop.dto.LoginRequest;
import com.vinylshop.dto.RegisterRequest;
import com.vinylshop.entity.RefreshToken;
import com.vinylshop.entity.Role;
import com.vinylshop.entity.User;
import com.vinylshop.exception.InvalidTokenException;
import com.vinylshop.exception.ResourceAlreadyExistException;
import com.vinylshop.exception.ResourceException;
import com.vinylshop.exception.ResourceNotFoundException;
import com.vinylshop.mapper.UserMapper;
import com.vinylshop.repository.RefreshTokenRepository;
import com.vinylshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationServiceClient notificationServiceClient;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistException("Цей акаунт вже використовується");
        }
        request.cleanFieldsFromExtraSpaces();

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(Role.USER);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        String verificationRefreshToken = tokenService.generateRefreshToken(user);
        notificationServiceClient.sendVerificationEmail(user.getEmail(), verificationRefreshToken);
    }

    public String verify(String authHeader) {
        User user = getUserFromHeader(authHeader);

        if (user.isVerified()) {
            return "User is already verified.";
        }
        user.setVerified(true);
        userRepository.save(user);
        return "User verified successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Не знайдено користувача"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Невірний пароль");
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        RefreshToken token = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        refreshTokenRepository.save(token);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token was expired");
        }

        User user = refreshToken.getUser();
        String newAccess = tokenService.generateAccessToken(user);
        String newRefresh = tokenService.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefresh)
                .user(user)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());

        return new AuthResponse(newAccess, newRefresh);
    }

    public void logout(String authHeader) {
        String refreshToken = authHeader.substring(7);
        tokenService.invalidateToken(refreshToken);
    }

    private User getUserFromHeader(String authHeader) {
        String token = authHeader.substring(7);
        return userRepository.findById(tokenService.getUserIdFromToken(token))
                .orElseThrow(() -> new ResourceException("User not found"));
    }

    public void updateEmail(String authHeader, String newEmail) {
        User user = getUserFromHeader(authHeader);

        if (userRepository.existsByEmail(newEmail)) {
            throw new ResourceAlreadyExistException("Email already in use");
        }

        String verificationEmailToken = tokenService.generateRefreshToken(user);

        // Відправляє на електронну пошту лист з посиланням дя верифікації
        notificationServiceClient.sendVerificationEmail(newEmail, verificationEmailToken);
    }

    public void verifyNewEmail(String authHeader, String newEmail) {
        User user = getUserFromHeader(authHeader);
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(ResourceNotFoundException::new);

        if (!user.isVerified()) {
            throw new ResourceException("User does not have a registered email");
        }

        String refreshToken = tokenService.generateRefreshToken(user);
        notificationServiceClient.sendPasswordResetEmail(email, refreshToken);
    }
}

