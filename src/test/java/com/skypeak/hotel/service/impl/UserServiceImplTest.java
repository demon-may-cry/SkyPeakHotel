package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.user.ChangeRoleRequest;
import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

/**
 * @author Дмитрий Ельцов
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса пользователей UserServiceImpl")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private RoleEntity createRole(Role roleName) {
        RoleEntity role = new RoleEntity();
        role.setName(roleName);
        return role;
    }

    private UserEntity createUser(String email, String password, Role roleName, Status status) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(createRole(roleName));
        user.setStatus(status);
        return user;
    }

    @Test
    @DisplayName("findAll должен возвращать пагинированный список пользователей")
    void findAll_ReturnsPaginatedUsers() {
        // Given
        List<UserEntity> users = IntStream.range(0, 10)
                .mapToObj(i -> createUser(
                        "user" + i + "@skypeak.com",
                        "password" + i,
                        Role.USER,
                        Status.ACTIVE))
                .toList();

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(users, pageable, users.size());

        given(userRepository.findAll(pageable)).willReturn(page);

        // When
        Page<UserEntity> result = userService.findAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getContent().getFirst().getEmail()).isEqualTo("user0@skypeak.com");

        then(userRepository).should().findAll(pageable);
    }

    @Test
    @DisplayName("getUserById должен возвращать пользователя, если он найден")
    void getUserById_ReturnUser() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        UserEntity result = userService.getUserById(user.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("user@skypeak.com");
        assertThat(result.getRole().getName()).isEqualTo(Role.USER);
        assertThat(result.getStatus()).isEqualTo(Status.ACTIVE);

        then(userRepository).should().findById(user.getId());
    }

    @Test
    @DisplayName("getUserById должен выбрасывать исключение, если пользователь не найден")
    void getUserById_ReturnException_WhenNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        given(userRepository.findById(id)).willReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID: " + id);

        then(userRepository).should().findById(id);
    }

    @Test
    @DisplayName("changeUserRole должен обновлять роль при валидном запросе")
    void changeUserRole_UpdateRole() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.MANAGER);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(roleRepository.findByName(Role.MANAGER)).willReturn(Optional.of(createRole(Role.MANAGER)));

        userService.changeUserRole(user.getId(), request.role());

        // Then
        assertThat(user.getRole().getName()).isEqualTo(Role.MANAGER);
        then(userRepository).should().findById(user.getId());
        then(roleRepository).should().findByName(Role.MANAGER);
    }

    @Test
    @DisplayName("changeUserRole должен выбрасывать исключение, если пользователь - администратор")
    void changeUserRole_ReturnException_WhenRoleAdmin() {
        // Given
        UserEntity user = createUser("admin@skypeak.com", "password", Role.ADMIN, Status.ACTIVE);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.MANAGER);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // Then
        assertThatThrownBy(() -> userService.changeUserRole(user.getId(), request.role()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Administrator role cannot be changed");
    }

    @Test
    @DisplayName("changeUserRole должен выбрасывать исключение, если у пользователя уже есть эта роль")
    void changeUserRole_ReturnException_WhenSameRole() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.USER);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // Then
        assertThatThrownBy(() -> userService.changeUserRole(user.getId(), request.role()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already has the role: " + request.role());
    }

    @Test
    @DisplayName("changeUserRole должен выбрасывать исключение, если роль не найдена")
    void changeUserRole_ReturnException_WhenRoleNotFound() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        ChangeRoleRequest request = new ChangeRoleRequest(Role.MANAGER);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(roleRepository.findByName(Role.MANAGER)).willReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> userService.changeUserRole(user.getId(), request.role()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Role not found: " + request.role());
    }

    @Test
    @DisplayName("deactivateUser должен устанавливать статус INACTIVE")
    void deactivateUser_Deactivates() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        userService.deactivateUser(user.getId());

        // Then
        assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
        then(userRepository).should().findById(user.getId());
    }

    @Test
    @DisplayName("deactivateUser должен выбрасывать исключение, если пользователь - администратор")
    void deactivateUser_ReturnException_WhenRoleAdmin() {
        // Given
        UserEntity user = createUser("admin@skypeak.com", "password", Role.ADMIN, Status.ACTIVE);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // Then
        assertThatThrownBy(() -> userService.deactivateUser(user.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Administrator status cannot be changed");
    }

    @Test
    @DisplayName("deactivateUser должен выбрасывать исключение, если пользователь уже неактивен")
    void deactivateUser_ReturnException_WhenAlreadyInactive() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.INACTIVE);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // Then
        assertThatThrownBy(() -> userService.deactivateUser(user.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is already inactive");
    }

    @Test
    @DisplayName("activateUser должен устанавливать статус ACTIVE")
    void activateUser_Activate() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.INACTIVE);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        userService.activateUser(user.getId());

        // Then
        assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
        then(userRepository).should().findById(user.getId());
    }

    @Test
    @DisplayName("activateUser должен выбрасывать исключение, если пользователь уже активен")
    void activateUser_ReturnException_WhenAlreadyActive() {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        // When
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // Then
        assertThatThrownBy(() -> userService.activateUser(user.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User is already active");
    }
}
