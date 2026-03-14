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

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userMapper.toDto(userService.getUserById(id));
    }

    @PatchMapping("/{id}/role")
    public void changeUserRole(@PathVariable UUID id,
                               @RequestBody @Valid ChangeRoleRequest request) {
        userService.changeUserRole(id, request.role());
    }

    @PatchMapping("/{id}/deactivate")
    public void deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
    }

    @PatchMapping("/{id}/activate")
    public void activateUser(@PathVariable UUID id) {
        userService.activateUser(id);
    }
}
