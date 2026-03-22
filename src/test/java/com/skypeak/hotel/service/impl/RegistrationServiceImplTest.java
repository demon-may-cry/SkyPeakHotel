package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.auth.RegisterRequest;
import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserBalanceRepository;
import com.skypeak.hotel.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса регистрации RegistrationServiceImpl")
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserBalanceRepository balanceRepository;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private static RegisterRequest getRegisterRequest() {
        var request = new RegisterRequest();
        request.setEmail("user@skypeak.com");
        request.setPassword("password");
        return request;
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void register_registersUser_whenEmailIsAvailableAndRoleExists() {
        // Given
        RegisterRequest request = getRegisterRequest();

        var userRole = new RoleEntity();
        userRole.setName(Role.USER);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(Role.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        // When
        registrationService.register(request);

        // Then
        verify(userRepository).save(any(UserEntity.class));
        verify(balanceRepository).save(any());
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(roleRepository, times(1)).findByName(Role.USER);
        verify(passwordEncoder, times(1)).encode(request.getPassword());
    }

    @Test
    @DisplayName("Исключение, если email уже занят")
    void register_throwsException_whenEmailAlreadyExists() {
        // Given
        RegisterRequest request = getRegisterRequest();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> registrationService.register(request));
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(roleRepository, passwordEncoder, balanceRepository);
    }

    @Test
    @DisplayName("Исключение, если роль USER не найдена")
    void register_throwsException_whenUserRoleNotFound() {
        // Given
        RegisterRequest request = getRegisterRequest();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName(Role.USER)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class, () -> registrationService.register(request));
        verify(userRepository, times(1)).existsByEmail(request.getEmail());
        verify(roleRepository, times(1)).findByName(Role.USER);
        verifyNoMoreInteractions(passwordEncoder, balanceRepository);
    }
}
