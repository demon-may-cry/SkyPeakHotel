package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.user.ChangeRoleRequest;
import com.skypeak.hotel.dto.user.UserResponse;
import com.skypeak.hotel.mapper.UserMapper;
import com.skypeak.hotel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/management/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class UserManagementController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.findAll(pageable)
                .map(userMapper::toDto);
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable UUID userId) {
        return userMapper.toDto(userService.getUserById(userId));
    }

    @PatchMapping("/{userId}/role")
    public void changeUserRole(@PathVariable UUID userId,
                               @RequestBody @Valid ChangeRoleRequest newRole) {
        userService.changeUserRole(userId, newRole);
    }

    @DeleteMapping("/{userId}/deactivate")
    public void deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
    }
}

