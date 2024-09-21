package com.izzy.service;

import com.izzy.exception.ResourceNotFoundException;
import com.izzy.exception.TokenRefreshException;
import com.izzy.model.RefreshToken;
import com.izzy.model.User;
import com.izzy.repository.RefreshTokenRepository;
import com.izzy.repository.UserRepository;
import com.izzy.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${izzy.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(JwtUtils jwtUtils, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Searches in storage given refresh token
     *
     * @param refreshToken token for checking
     * @return existing in storage token on success
     */
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    public ResponseCookie refreshAccessTokenCookie(String refreshToken){
        return findByToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(jwtUtils::generateJwtCookie)
                .orElseThrow(() -> new TokenRefreshException(refreshToken,
                        "Refresh token cannot be found or expired!"));
    }

    /**
     * Creates new refresh token for user
     *
     * @param userId user id whose token should be created
     * @return created refresh token
     * @throws ResourceNotFoundException if user not found in storage
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User", "id", userId));
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId).orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setCurrentToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Check for token expiration and remove it from storage on expire
     *
     * @param token verifying token
     * @return token on success
     * @throws TokenRefreshException if refresh token expired
     */
    @Transactional
    public RefreshToken verifyExpiration(@NonNull RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getCurrentToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    /**
     * Remove user refresh token from database
     *
     * @param userId user id whose token should be deleted
     * @return Non-null ID of the removed line on success; 0 otherwise.
     */
    @Transactional
    public int deleteByUserId(Long userId) {
        return userRepository.findById(userId).map(refreshTokenRepository::deleteByUser).orElse(0);
    }
}
