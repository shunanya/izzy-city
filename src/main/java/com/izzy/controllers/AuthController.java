package com.izzy.controllers;

import com.izzy.exception.TokenRefreshException;
import com.izzy.model.RefreshToken;
import com.izzy.model.Role;
import com.izzy.model.User;
import com.izzy.payload.request.LoginRequest;
import com.izzy.payload.request.SignupRequest;
import com.izzy.payload.response.MessageResponse;
import com.izzy.payload.response.UserInfoResponse;
import com.izzy.repository.RoleRepository;
import com.izzy.security.jwt.JwtUtils;
import com.izzy.service.AuthService;
import com.izzy.service.RefreshTokenService;
import com.izzy.service.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/izzy/auth")
public class AuthController {
    final
    AuthenticationManager authenticationManager;
    final
    JwtUtils jwtUtils;
    final
    RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final RoleRepository roleRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, RefreshTokenService refreshTokenService, AuthService authService, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.authService = authService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest userRequest) {
        if (authService.existByUserIdentifier(userRequest.getPhonenumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if (authService.existByUserIdentifier(userRequest.getPhonenumber())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: phone number is already in use!"));
        }
        User user = new User();
        user.setFirstName("Dummy");
        user.setPhoneNumber(userRequest.getPhonenumber());
        user.setPassword(userRequest.getPassword());
        Set<String> strRoles = userRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles != null && !strRoles.isEmpty()) {
            strRoles.forEach(role -> {
                Optional<Role> existingRole = roleRepository.findByName(role);
                existingRole.ifPresent(roles::add);
            });
            user.setRoles(roles);
        }
        if (roles.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse(("Error: user role is not defined correctly.")));
        }
        User savedUser = authService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        User user = authService.login(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        if (user != null) {
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getCurrentToken());
            Set<Role> set = user.getRoles();
            List<String> roles = new ArrayList<>(1);
            set.forEach(role -> roles.add(role.getName()));
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new UserInfoResponse(user.getId(),
                            user.getFirstName(),
                            user.getPhoneNumber(),
                            roles));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Provided credentials are wrong."));
    }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if ( !"anonymousUser".equalsIgnoreCase(principle.toString())) {
      Long userId = ((UserPrincipal) principle).getId();
      refreshTokenService.deleteByUserId(userId);
    } else {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Already signed-out"));
    }
    
    ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
    ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request) {
    String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
    
    if ((refreshToken != null) && (refreshToken.length() > 0)) {
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
              "Refresh token is not in database!"));
    }
    
    return ResponseEntity.badRequest().body(new MessageResponse("Error: Refresh Token is empty!"));
  }

}
