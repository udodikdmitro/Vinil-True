package com.vinylshop.service;

import com.vinylshop.dto.AuthResponse;
import com.vinylshop.dto.LoginRequest;
import com.vinylshop.dto.RegisterRequest;
import com.vinylshop.entity.RefreshToken;
import com.vinylshop.entity.Role;
import com.vinylshop.entity.User;
import com.vinylshop.repository.RefreshTokenRepository;
import com.vinylshop.repository.UserRepository;
import com.vinylshop.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Користувач з такою поштою вже існує");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Set.of(Role.USER))
                .build();
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Не знайдено користувача"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Невірний пароль");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

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
                .orElseThrow(() -> new RuntimeException("Недійсний токен"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Токен прострочено");
        }

        User user = refreshToken.getUser();
        String access = jwtProvider.generateAccessToken(user.getEmail(), user.getRoles());
        String newRefresh = jwtProvider.generateRefreshToken(user.getEmail());

        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefresh)
                .user(user)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());

        return new AuthResponse(access, newRefresh);
    }

    //logout
}

