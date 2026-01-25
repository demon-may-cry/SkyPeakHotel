package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.auth.LoginRequest;
import com.skypeak.hotel.dto.auth.LoginResponse;
import com.skypeak.hotel.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        String role = Objects.requireNonNull(authentication.getAuthorities().stream()
                        .findFirst()
                        .orElseThrow()
                        .getAuthority())
                        .replace("ROLE_", "");
        String jwt = jwtService.generateToken(request.getEmail(), role);

        return ResponseEntity.ok(new LoginResponse(jwt, "Bearer"));
    }
}
