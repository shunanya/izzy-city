package com.izzy.service;

import com.izzy.model.RefreshToken;
import com.izzy.model.User;
import com.izzy.repository.RefreshTokenRepository;
import com.izzy.repository.UserRepository;
import com.izzy.exception.TokenRefreshException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${izzy.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Retrieve the existing refresh token by user ID
            Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUserId(userId);

            if (refreshTokenOptional.isPresent()) {
                refreshToken = refreshTokenOptional.get();
            } else {
                refreshToken.setUser(userRepository.findById(userId).get());
            }
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken.setCurrentToken(UUID.randomUUID().toString());
            refreshToken = refreshTokenRepository.save(refreshToken);
            return refreshToken;
        }
        return null;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getCurrentToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
