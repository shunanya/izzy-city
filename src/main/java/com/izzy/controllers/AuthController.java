package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.exception.TokenRefreshException;
import com.izzy.security.utils.Utils;
import com.izzy.model.RefreshToken;
import com.izzy.model.User;
import com.izzy.payload.request.LoginRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.payload.response.UserShortInfo;
import com.izzy.security.jwt.JwtUtils;
import com.izzy.service.AuthService;
import com.izzy.service.RefreshTokenService;
import com.izzy.service.user_details.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller class responsible for handling authentication-related requests such as sign-in, sign-out, and token refresh.
 * It utilizes services like {@link AuthService}, {@link RefreshTokenService}, and {@link JwtUtils} to perform authentication operations.
 */
@RestController
@RequestMapping("/izzy/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    public AuthController(JwtUtils jwtUtils, RefreshTokenService refreshTokenService, AuthService authService) {
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
    }

    /**
     * Handles the sign-in request by authenticating the user with the provided credentials.
     *
     * @param loginRequestString JSON string representation of the login request payload (phone number and password).
     * @return ResponseEntity containing the user information and cookies with JWT tokens upon successful authentication.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody String loginRequestString) {
        try {
            // Validate request body
            LoginRequest loginRequest = (new ObjectMapper()).readValue(loginRequestString, LoginRequest.class);
            //processing
            User user = authService.login(loginRequest.getPhoneNumber(), loginRequest.getPassword());
            if (user != null) {
                ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
                ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getCurrentToken());
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                        .body(new UserShortInfo(user));
            }
            throw new CredentialsExpiredException("Error: Provided credentials are wrong.");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Handles the sign-out request by invalidating the user's session and clearing JWT cookies.
     *
     * @param request HttpServletRequest object to access cookies.
     * @return ResponseEntity indicating successful sign-out along with cleared cookies.
     */
    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            String token = jwtUtils.getJwtFromCookies(request);
            if (token == null || token.isBlank()) {
                throw new SecurityException("Error: Seems user already signed-out or tokens expired");
            }
            String ident = jwtUtils.getUserIdentifierFromJwtToken(token);
            UserPrincipal principle = authService.getUserByUserIdentifier(ident);
            if (principle != null) {
                refreshTokenService.deleteByUserId(principle.getId());
                ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
                ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                        .body(new MessageResponse("You've been signed out!"));
            } else {
                throw new SecurityException("Error: Seems user already signed-out or tokens expired");
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }

    /**
     * Handles the token refresh request by validating the refresh token and issuing a new JWT.
     *
     * @param request HttpServletRequest object to access cookies.
     * @return ResponseEntity indicating successful token refresh along with new JWT cookie.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new SecurityException("Error: Refresh tokens expired");
            }
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token cannot be found or expired!"));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}
