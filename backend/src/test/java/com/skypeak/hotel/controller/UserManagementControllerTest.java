package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.user.ChangeRoleRequest;
import com.skypeak.hotel.dto.user.UserResponse;
import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.mapper.UserMapper;
import com.skypeak.hotel.security.jwt.JwtAuthenticationFilter;
import com.skypeak.hotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Дмитрий Ельцов
 */
@WebMvcTest(UserManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Тесты контроллера управления пользователями UserManagementController")
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ChangeRoleRequest changeRoleRequest(Role role) {
        return new ChangeRoleRequest(role);
    }

    private RoleEntity createRole() {
        var role = new RoleEntity();
        role.setName(Role.USER);
        return role;
    }

    private UserEntity createUser(String email, String password) {
        var user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(createRole());
        user.setStatus(Status.ACTIVE);
        return user;
    }

    @Test
    @DisplayName("GET /users - возвращает пагинированный список пользователей")
    void getAllUsers_returnsPaginatedUsers() throws Exception {
        // Given
        List<UserEntity> users = IntStream.range(0, 10)
                .mapToObj(i -> createUser(
                        "user" + i + "@skypeak.com",
                        "password" + i
                ))
                .toList();

        Page<UserEntity> page = new PageImpl<>(users);

        given(userService.findAll(any(Pageable.class))).willReturn(page);
        given(userMapper.toDto(any(UserEntity.class)))
                .willAnswer(invocation -> {
                    UserEntity user = invocation.getArgument(0);
                    return new UserResponse(
                            user.getId(),
                            user.getPhoneNumber(),
                            user.getEmail(),
                            user.getStatus().name(),
                            user.getRole().getName().name(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getMiddleName(),
                            user.getBirthDate(),
                            user.getAvatarUrl(),
                            user.getCreatedAt(),
                            user.getLastLoginAt());
                });

        // When
        mockMvc.perform(get("/api/v1/management/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].email").value("user0@skypeak.com"))
                .andExpect(jsonPath("$.content[0].status").value(Status.ACTIVE.name()))
                .andExpect(jsonPath("$.content[0].role").value(Role.USER.name()))
                .andExpect(jsonPath("$.content[9].email").value("user9@skypeak.com"))
                .andExpect(jsonPath("$.content[9].status").value(Status.ACTIVE.name()))
                .andExpect(jsonPath("$.content[9].role").value(Role.USER.name()));

        // Then
        then(userService).should().findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /users/{id} - возвращает пользователя по ID")
    void getUserById_returnsUser_whenExists() throws Exception {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password");

        var response = new UserResponse(
                user.getId(),
                "+79998887766",
                "user@skypeak.com",
                Status.ACTIVE.name(),
                Role.USER.name(),
                "firstName",
                "lastName",
                "middleName",
                null,
                null,
                null,
                null
        );

        given(userService.getUserById(user.getId())).willReturn(user);
        given(userMapper.toDto(any(UserEntity.class))).willReturn(response);

        // When
        mockMvc.perform(get("/api/v1/management/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@skypeak.com"))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.name()))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));

        // Then
        then(userService).should().getUserById(user.getId());
        then(userMapper).should().toDto(any(UserEntity.class));
    }

    @Test
    @DisplayName("GET /users/{id} - возвращает 404, если пользователь не найден")
    void getUserById_returnsNotFound_whenUserDoesNotExist() throws Exception {
        // Given
        given(userService.getUserById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("User not found"));

        // When
        mockMvc.perform(get("/api/v1/management/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        // Then
        then(userService).should().getUserById(any(UUID.class));
    }

    @Test
    @DisplayName("PATCH /users/{id}/role - успешно изменяет роль пользователя")
    void changeUserRole_changesRole_whenRequestIsValid() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/v1/management/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest(Role.MANAGER))))
                .andExpect(status().isOk());

        // Then
        then(userService).should().changeUserRole(id, Role.MANAGER);
    }

    @Test
    @DisplayName("PATCH /users/{id}/role - возвращает 400 для невалидной роли")
    void changeUserRole_returnsBadRequest_forInvalidRole() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        String invalidRoleJson = """
                {
                    "role" : "INVALID_ROLE"
                }
                """;

        // When & Then
        mockMvc.perform(patch("/api/v1/management/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRoleJson))
                .andExpect(status().isBadRequest());

        // Then
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("PATCH /users/{id}/role - возвращает 404, если пользователь не найден")
    void changeUserRole_returnsNotFound_whenUserDoesNotExist() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        doThrow(new EntityNotFoundException("User not found"))
                .when(userService).changeUserRole(any(UUID.class), any(Role.class));

        // When
        mockMvc.perform(patch("/api/v1/management/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeRoleRequest(Role.USER))))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("PATCH /users/{id}/deactivate - успешно деактивирует пользователя")
    void deactivateUser_deactivatesUser_whenRequestIsValid() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/v1/management/users/{id}/deactivate", id))
                .andExpect(status().isOk());

        // Then
        then(userService).should().deactivateUser(id);
    }

    @Test
    @DisplayName("PATCH /users/{id}/activate - успешно активирует пользователя")
    void activateUser_activatesUser_whenRequestIsValid() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/v1/management/users/{id}/activate", id))
                .andExpect(status().isOk());

        // Then
        then(userService).should().activateUser(id);
    }
}
