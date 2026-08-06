package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.user.UpdateUserProfileRequest;
import com.skypeak.hotel.mapper.UserMapper;
import com.skypeak.hotel.service.UserService;
import com.skypeak.hotel.dto.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Получает профиль текущего пользователя.
     *
     * @return {@link UserResponse} с данными профиля.
     */
    @GetMapping("/me")
    public UserResponse getUserProfile() {
        log.info("▶️ Получен запрос на получение профиля пользователя");
        String email = getCurrentUserEmail();
        UserResponse response = userMapper.toDto(userService.getUserByEmail(email));
        log.info("✅ Профиль пользователя {} успешно получен", email);
        return response;
    }

    /**
     * Обновляет данные профиля пользователя.
     *
     * @param request DTO с обновленными данными профиля.
     */
    @PutMapping("/me")
    public void updateUserProfile(@RequestBody @Valid UpdateUserProfileRequest request) {
        log.info("▶️ Получен запрос на обновление профиля пользователя");
        String email = getCurrentUserEmail();
        userService.updateUserProfile(email, request);
        log.info("✅ Профиль пользователя {} успешно обновлен", email);
    }

    private String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException(
                    "Пользователь не аутентифицирован"
            );
        }

        return authentication.getName();
    }
}

