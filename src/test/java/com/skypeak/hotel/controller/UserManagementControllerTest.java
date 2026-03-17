package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.user.UserResponse;
import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.mapper.UserMapper;
import com.skypeak.hotel.service.UserService;
import com.skypeak.hotel.security.jwt.JwtAuthenticationFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Дмитрий Ельцов
 */
@WebMvcTest(UserManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private RoleEntity createRole() {
        RoleEntity role = new RoleEntity();
        role.setName(Role.USER);
        return role;
    }

    private UserEntity createUser(String email, String password) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(createRole());
        user.setStatus(Status.ACTIVE);
        return user;
    }

    @Test
    @DisplayName("GET /api/management/users returns paginated users")
    void getAllUsers_ReturnPaginatedUsers() throws Exception {
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
                            user.getEmail(),
                            user.getStatus().name(),
                            user.getRole().getName().name());
                });

        // When
        mockMvc.perform(get("/api/management/users")
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
    @DisplayName("GET /api/management/users/{id} returns user by ID")
    void getUserById_ReturnsUser_WhenExists() throws Exception {
        // Given
        UserEntity user = createUser("user@skypeak.com", "password");

        UserResponse response = new UserResponse(
                user.getId(),
                "user@skypeak.com",
                Status.ACTIVE.name(),
                Role.USER.name()
        );

        given(userService.getUserById(user.getId())).willReturn(user);
        given(userMapper.toDto(any(UserEntity.class))).willReturn(response);

        // When
        mockMvc.perform(get("/api/management/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@skypeak.com"))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.name()))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));

        // Then
        then(userService).should().getUserById(user.getId());
        then(userMapper).should().toDto(any(UserEntity.class));
    }

    @Test
    @DisplayName("GET /api/management/users/{id} returns 404 when user not found")
    void getUserById_Returns404_WhenNotFound() throws Exception {
        // Given
        given(userService.getUserById(any(UUID.class)))
                .willThrow(new EntityNotFoundException("User not found"));

        // When
        mockMvc.perform(get("/api/management/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        // Then
        then(userService).should().getUserById(any(UUID.class));
    }

    @Test
    @DisplayName("PATCH /api/management/users/{id}/role changes user role")
    void changeUserRole_ChangesRole() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/management/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "role": "MANAGER"
                                }
                                """))
                .andExpect(status().isOk());

        // Then
        then(userService).should().changeUserRole(id, Role.MANAGER);
    }

    @Test
    @DisplayName("PATCH /api/management/users/{id}/role returns 400 for invalid role")
    void changeUserRole_ReturnsBadRequest_ForInvalidRole() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/management/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "role": "INVALID"
                                }
                                """))
                .andExpect(status().isBadRequest());

        // Then
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("PATCH /api/management/users/{id}/role returns 404 when user not found")
    void changeUserRole_ReturnsNotFound() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        doThrow(new EntityNotFoundException("User not found"))
                .when(userService).changeUserRole(id, Role.MANAGER);

        // When
        mockMvc.perform(patch("/api/management/users/{id}/role", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\": \"MANAGER\"}"))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("PATCH /api/management/users/{id}/deactivate deactivates user")
    void deactivateUser_Deactivates() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/management/users/{id}/deactivate", id))
                .andExpect(status().isOk());

        // Then
        then(userService).should().deactivateUser(id);
    }

    @Test
    @DisplayName("PATCH /api/management/users/{id}/activate activates user")
    void activateUser_Activate() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        // When
        mockMvc.perform(patch("/api/management/users/{id}/activate", id))
                .andExpect(status().isOk());

        // Then
        then(userService).should().activateUser(id);
    }
}
