package com.izzy.security.jwt;

import com.izzy.model.User;
import com.izzy.service.user_details.UserPrincipal;
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
     * Create cpokie contain refresh token
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
     * @throws Exception in case not cookie or token expired
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
     * @throws Exception in case not cookie or token expired
     */
    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        String refreshToken = getCookieValueByName(request, jwtRefreshCookie);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new SecurityException("Error: Refresh tokens expired");
        }
        return refreshToken;
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null).path("/izzy").build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null).path("/izzy/auth/refresh").build();
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value).path(path).maxAge(24 * 60 * 60).httpOnly(true).build();
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
     * @throws Exception while token invalid
     */
    public boolean validateJwtToken(String authToken) {
//        try {
        Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
        return true;
//        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            logger.error("JWT token is expired: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            logger.error("JWT token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty: {}", e.getMessage());
//        }
//
//        return false;
    }

    /**
     * Parse JWT access token and return user identifier
     *
     * @param token JWT access token
     * @return the user unique identifier
     */
    public String getUserIdentifierFromJwtToken(String token) {
        return validateJwtToken(token) ? Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject()
                : null;
    }

    /**
     * Create Access Token by using user identifier
     *
     * @param userIdentifier any user unique identifier (in our case - phone number)
     * @return JWT access token
     */
    public String generateTokenFromUserIdentifier(String userIdentifier) {
        return Jwts.builder()
                .setSubject(userIdentifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}
