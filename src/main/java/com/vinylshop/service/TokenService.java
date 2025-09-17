package com.vinylshop.service;

import com.vinylshop.dto.TokenResponse;
import com.vinylshop.entity.Token;
import com.vinylshop.entity.User;
import com.vinylshop.exception.ResourceException;
import com.vinylshop.repository.TokenRepository;
import com.vinylshop.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.accessToken.expirationMs}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshToken.expirationMs}")
    private long refreshTokenExpirationMs;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public TokenService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Генерує Access Token.
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole().name())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Генерує Refresh Token і зберігає його в базі даних.
     */
    public String generateRefreshToken(User user) {
        String newRefreshToken = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("role", user.getRole().name())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        Token token = new Token();
        token.setRefreshToken(newRefreshToken);
        token.setUser(user);
        token.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        tokenRepository.save(token);

        return newRefreshToken;
    }

    /**
     * Оновлює токени (access та refresh) на основі Refresh Token.
     */
    @Transactional
    public TokenResponse updateTokens(String authHeader) {
        String refreshToken = authHeader.substring(7);
        User user = userRepository.findById(getUserIdFromToken(refreshToken))
                .orElseThrow(() -> new ResourceException("Invalid credentials"));

        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * Відкликання токенів (logout).
     */
    public void invalidateToken(String refreshToken) {
        tokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(tokenRepository::delete);
    }

    /**
     * Витягує ID користувача з Token.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Scheduled(fixedRate = 36000000)
    @Transactional
    public void deleteExpiredTokens() {
        Instant now = Instant.now();
        tokenRepository.deleteAllExpiredTokens(now);
        log.debug("Deleted expired tokens");
    }
}