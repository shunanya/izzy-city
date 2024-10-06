package com.izzy.security.jwt;

import com.izzy.model.User;
import com.izzy.service.user_details.UserPrincipal;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {
//    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value(value = "${izzy.app.jwtSecret}")
    private String jwtSecret;

    @Value(value = "${izzy.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value(value = "${izzy.app.jwtCookieName}")
    private String jwtCookie;

    @Value(value = "${izzy.app.jwtRefreshCookieName}")
    private String jwtRefreshCookie;

    @Value(value = "${izzy.app.jwtRefreshExpirationMs}")
    private Long jwtRefreshExpirationMs;

    /**
     * Create cookie that contain JWT access token
     *
     * @param userPrincipal User details {@link UserPrincipal}
     * @return Cookie
     */
    public ResponseCookie generateJwtCookie(UserPrincipal userPrincipal) {
        String jwt = generateTokenFromUserIdentifier(userPrincipal.getPhoneNumber());
        return generateCookie(jwtCookie, jwt, "/izzy");
    }

    /**
     * Create cookie that contain JWT access token
     *
     * @param user User object
     * @return Cookie that contains JWT access token with user identifier
     */
    public ResponseCookie generateJwtCookie(User user) {
        String accessToken = generateTokenFromUserIdentifier(user.getPhoneNumber());
        return generateCookie(jwtCookie, accessToken, "/izzy");
    }

    /**
     * Create cookie that contain JWT access token
     *
     * @param accessToken JWT access token
     * @return Cookie that contains JWT access token
     */
    public ResponseCookie generateJwtCookie(String accessToken){
        return generateCookie(jwtCookie, accessToken, "/izzy");
    }

    /**
     * Create cookie contain refresh token
     *
     * @param refreshToken refresh token
     * @return Cookie that contains refresh token
     */
    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/izzy/auth/refresh");
    }

    /**
     * Retrieve JWT access token from cookie
     *
     * @param request Request object containing cookie
     * @return JWT access token on success
     * @throws SecurityException in case not cookie or token expired
     */
    public String getJwtFromCookies(HttpServletRequest request) {
        String token = getCookieValueByName(request, jwtCookie);
        if (token == null || token.isBlank()) {
            throw new SecurityException("Error: Seems user already signed-out or tokens expired");
        }
        return token;
    }

    /**
     * Retrieve refresh token from cookie
     *
     * @param request Request object containing cookie
     * @return the refresh token
     * @throws SecurityException in case not cookie or token expired
     */
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        String refreshToken = getCookieValueByName(request, jwtRefreshCookie);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new SecurityException("Error: Refresh tokens expired");
        }
        return refreshToken;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/izzy").maxAge(0).build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null).path("/izzy/auth/refresh").maxAge(0).build();
    }
//TODO replace to 'sameSite("Lax") in production
    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(Duration.ofMillis(jwtRefreshExpirationMs).toSeconds())
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return (cookie == null) ? null : cookie.getValue();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Check validity of JWT access token
     *
     * @param authToken JWT access token
     * @return true on success
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(key())  // Set the key used for verification
                    .build()
                    .parseClaimsJws(authToken);  // If token is invalid, this will throw an exception
            return true;
        } catch (JwtException e) {
            // Token is invalid, could be expired or tampered with
            return false;
        }
    }
    /**
     * Parse JWT access token and return user identifier
     *
     * @param token JWT access token
     * @return the user unique identifier
     */
    public String getUserIdentifierFromJwtToken(String token) {
        return validateJwtToken(token) ?
                Jwts.parser()
                        .setSigningKey(key())  // Set the key used for signing the token
                        .build()
                        .parseClaimsJws(token)  // Parse the token to extract the claims
                        .getBody()
                        .getSubject()  // Extract the subject (userIdentifier)
                : null;
    }
    /**
     * Create Access Token by using user identifier
     *
     * @param userIdentifier any user unique identifier (in our case - phone number)
     * @return JWT access token
     */
    public String generateTokenFromUserIdentifier(String userIdentifier) {
        Instant now = Instant.now();  // Current timestamp using Instant
        Instant expiration = now.plusMillis(jwtExpirationMs);  // Calculate expiration time

        return Jwts.builder()
                .setSubject(userIdentifier)
                .setIssuedAt(Date.from(now))  // Use Instant for current timestamp
                .setExpiration(Date.from(expiration))  // Set expiration using Instant
                .signWith(key(), SignatureAlgorithm.HS256)  // Sign with key and algorithm
                .compact();
    }
}
