package com.izzy.controllers;

import com.izzy.model.User;
import com.izzy.model.RefreshToken;
import com.izzy.payload.request.LoginRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.payload.response.UserInfoResponse;
import com.izzy.service.AuthService;
import com.izzy.service.UserDetailsImpl;
import com.izzy.service.RefreshTokenService;
import com.izzy.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (authService.existByUserIdentifier(user.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        User savedUser = authService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/signin")
//    public ResponseEntity<UserEntity> loginUser(@RequestBody LoginRequest loginRequest) {
//        UserEntity user = authService.login(loginRequest.getPhoneNumber(), loginRequest.getPassword());
//        if (user != null) {
//            return ResponseEntity.ok(user);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//    }
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getCurrentToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getPhoneNumber(),
                        roles));
    }

}