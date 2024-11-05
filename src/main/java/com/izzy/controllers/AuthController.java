package com.izzy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.model.RefreshToken;
import com.izzy.model.User;
import com.izzy.payload.request.LoginRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.payload.response.UserInfo;
import com.izzy.security.jwt.JwtUtils;
import com.izzy.security.utils.Utils;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*/

/**
 * Controller class responsible for handling authentication-related requests such as sign-in, sign-out, and token refresh.
 * It utilizes services like {@link AuthService}, {@link RefreshTokenService}, and {@link JwtUtils} to perform authentication operations.
 */
@RestController
@RequestMapping("/izzy/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
//                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .body(new UserInfo(user, authService.getUserById(user.getUserManager()), true));
            }
            throw new CredentialsExpiredException("Error: Provided credentials are wrong.");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
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
            String ident = jwtUtils.getUserIdentifierFromJwtToken(token);
            if (ident != null && !ident.isBlank()) {
                UserPrincipal principle = authService.getUserByUserIdentifier(ident);
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
            logger.error(ex.getMessage());
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
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE,
                            refreshTokenService.refreshAccessTokenCookie(jwtUtils.getJwtRefreshFromCookies(request)).toString())
                    .body(new MessageResponse("Token is refreshed successfully!"));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Utils.substringErrorFromException(ex));
        }
    }
}
