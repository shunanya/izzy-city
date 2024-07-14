package com.izzy.security.jwt;

import com.izzy.model.UserEntity;
import com.izzy.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value(value = "${izzy.app.jwtSecret}")
  private String jwtSecret;

  @Value(value = "${izzy.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value(value = "${izzy.app.jwtCookieName}")
  private String jwtCookie;
  
  @Value(value = "${izzy.app.jwtRefreshCookieName}")
  private String jwtRefreshCookie;

  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
    String jwt = generateTokenFromUserIdentifier(userPrincipal.getPhoneNumber());
    return generateCookie(jwtCookie, jwt, "/izzy");
  }
  
  public ResponseCookie generateJwtCookie(UserEntity user) {
    String jwt = generateTokenFromUserIdentifier(user.getPhoneNumber());
    return generateCookie(jwtCookie, jwt, "/izzy");
  }
  
  public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
    return generateCookie(jwtRefreshCookie, refreshToken, "/izzy/auth/refresh");
  }
  
  public String getJwtFromCookies(HttpServletRequest request) {
    return getCookieValueByName(request, jwtCookie);
  }
  
  public String getJwtRefreshFromCookies(HttpServletRequest request) {
    return getCookieValueByName(request, jwtRefreshCookie);
  }

  public ResponseCookie getCleanJwtCookie() {
    return ResponseCookie.from(jwtCookie, null).path("/izzy").build();
  }
  
  public ResponseCookie getCleanJwtRefreshCookie() {
    return ResponseCookie.from(jwtRefreshCookie, null).path("/izzy/auth/refresh").build();
  }

  public String getUserIdentifierFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }
  
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
  
  public String generateTokenFromUserIdentifier(String userIdentifier) {
    return Jwts.builder()
        .setSubject(userIdentifier)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }
    
  private ResponseCookie generateCookie(String name, String value, String path) {
    return ResponseCookie.from(name, value).path(path).maxAge(24 * 60 * 60).httpOnly(true).build();
  }
  
  private String getCookieValueByName(HttpServletRequest request, String name) {
    Cookie cookie = WebUtils.getCookie(request, name);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }
}
