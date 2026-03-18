package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.auth.LoginRequest;
import com.skypeak.hotel.dto.auth.LoginResponse;
import com.skypeak.hotel.dto.auth.RegisterRequest;
import com.skypeak.hotel.dto.auth.RegisterResponse;
import com.skypeak.hotel.security.jwt.JwtService;
import com.skypeak.hotel.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * <p>
 * Предоставляет публичные эндпоинты для входа в систему и создания новой учетной записи.
 *
 * @author Дмитрий Ельцов
 * @see RegistrationService
 * @see JwtService
 * @see AuthenticationManager
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RegistrationService registrationService;

    /**
     * Аутентифицирует пользователя и возвращает JWT токен.
     *
     * @param request DTO с учетными данными (email и пароль).
     * @return {@link ResponseEntity} с {@link LoginResponse}, содержащим JWT токен и его тип.
     * @throws org.springframework.security.core.AuthenticationException если учетные данные неверны.
     * @see LoginRequest
     * @see LoginResponse
     * @see JwtService#generateToken(String, String)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("▶️ Попытка аутентификации пользователя с email: {}", request.getEmail());

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

        log.info("✅ Успешная аутентификация для {}. Выдан JWT токен.", request.getEmail());
        return ResponseEntity.ok(new LoginResponse(jwt, "Bearer"));
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param request DTO с данными для регистрации (email и пароль).
     * @return {@link ResponseEntity} с {@link RegisterResponse}, подтверждающим успешную регистрацию.
     * @throws IllegalStateException если пользователь с таким email уже существует.
     * @see RegisterRequest
     * @see RegisterResponse
     * @see RegistrationService#register(RegisterRequest)
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest request
            ) {
        log.info("▶️ Получен запрос на регистрацию пользователя с email: {}", request.getEmail());
        registrationService.register(request);
        log.info("✅ Запрос на регистрацию для {} успешно обработан.", request.getEmail());
        return ResponseEntity.ok(
                new RegisterResponse("Пользователь успешно зарегистрирован!")
        );
    }
}
