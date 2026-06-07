package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.user.UpdateUserProfileRequest;
import com.skypeak.hotel.mapper.UserMapper;
import com.skypeak.hotel.service.UserService;
import com.skypeak.hotel.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Получает профиль текущего пользователя по его ID.
     *
     * @param id UUID пользователя.
     * @return {@link UserResponse} с данными профиля.
     */
    @GetMapping("/{id}")
    public UserResponse getUserProfile(@PathVariable UUID id) {
        log.info("▶️ Получен запрос на получение профиля пользователя: {}", id);
        UserResponse response = userMapper.toDto(userService.getUserById(id));
        log.info("✅ Профиль пользователя {} успешно получен", id);
        return response;
    }

    /**
     * Обновляет данные профиля пользователя.
     *
     * @param id      UUID пользователя.
     * @param request DTO с обновленными данными профиля.
     */
    @PutMapping("/{id}")
    public void updateUserProfile(@PathVariable UUID id,
                                  @RequestBody @Valid UpdateUserProfileRequest request) {
        log.info("▶️ Получен запрос на обновление профиля пользователя: {}", id);
        userService.updateUserProfile(id, request);
        log.info("✅ Профиль пользователя {} успешно обновлен", id);
    }
}

