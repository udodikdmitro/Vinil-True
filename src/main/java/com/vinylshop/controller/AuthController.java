package com.vinylshop.controller;

import com.vinylshop.dto.*;
import com.vinylshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Користувача зареєстровано");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestHeader("Authorization") String authHeader) {
        String response = authService.verify(authHeader);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-tokens")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok("Logged out successfully.");
    }

    @PatchMapping("/updateEmail/{email}")
    public void updateEmail(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid UpdateEmailRequest request) {
        authService.updateEmail(authHeader, request.getNewEmail());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String newEmail) {
        authService.verifyNewEmail(authHeader, newEmail);
        return ResponseEntity.ok("Your email has been successfully updated");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to email.");
    }
}

