package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.auth.LoginRequest;
import com.skypeak.hotel.dto.auth.RegisterRequest;
import com.skypeak.hotel.security.jwt.JwtAuthenticationFilter;
import com.skypeak.hotel.security.jwt.JwtService;
import com.skypeak.hotel.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("Тесты контроллера AuthController")
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static LoginRequest getLoginRequest() {
        var request = new LoginRequest();
        request.setEmail("user@skypeak.com");
        request.setPassword("password");
        return request;
    }

    private static RegisterRequest getRegisterRequest() {
        var request = new RegisterRequest();
        request.setEmail("user@skypeak.com");
        request.setPassword("password");
        return request;
    }

    @Test
    @DisplayName("POST /login - Успешная аутентификация")
    void login_returnsToken_whenCredentialsAreValid() throws Exception {
        // Given
        LoginRequest request = getLoginRequest();

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword(),
                        authorities
                );

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(jwtService.generateToken("user@skypeak.com", "USER")).willReturn("test.jwt.token");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("test.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken("user@skypeak.com", "USER");
    }

    @Test
    @DisplayName("POST /login - Ошибка аутентификации (неверные данные)")
    void login_returnsUnauthorized_whenCredentialsAreInvalid() throws Exception {
        // Given
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new BadCredentialsException("Неверные учетные данные"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getLoginRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /register - Успешная регистрация")
    void register_returnsSuccessMessage_whenRegistrationIsSuccessful() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getRegisterRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Пользователь успешно зарегистрирован!"));

        verify(registrationService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /register - Ошибка регистрации (email занят)")
    void register_returnsError_whenEmailAlreadyExists() throws Exception {
        // Given
        doThrow(new IllegalStateException("Пользователь с таким email уже существует"))
                .when(registrationService).register(any(RegisterRequest.class));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getRegisterRequest())))
                .andExpect(status().isConflict());
    }
}
