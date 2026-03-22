package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.user.ChangeRoleRequest;
import com.skypeak.hotel.dto.user.UserResponse;
import com.skypeak.hotel.mapper.UserMapper;
import com.skypeak.hotel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Контроллер для управления пользователями.
 * <p>
 * Предоставляет эндпоинты для просмотра, изменения ролей и статуса пользователей.
 * Доступ к эндпоинтам ограничен ролями MANAGER и ADMIN.
 *
 * @author Дмитрий Ельцов
 * @see UserService
 * @see UserMapper
 * @see PreAuthorize
 */
@RestController
@RequestMapping("/api/v1/management/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@Slf4j
public class UserManagementController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Возвращает пагинированный список всех пользователей.
     *
     * @param pageable параметры пагинации (page, size, sort).
     * @return {@link Page} с {@link UserResponse}.
     */
    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("▶️ Получен запрос на получение списка пользователей. Параметры пагинации: {}", pageable);
        var users = userService.findAll(pageable).map(userMapper::toDto);
        log.info("✅ Успешно возвращен список из {} пользователей на странице {}",
                users.getNumberOfElements(),
                users.getNumber());
        return users;
    }

    /**
     * Возвращает пользователя по его уникальному идентификатору (ID).
     *
     * @param id UUID пользователя.
     * @return {@link UserResponse} с данными пользователя.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь не найден.
     */
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        log.info("▶️ Получен запрос на получение пользователя по ID: {}", id);
        var user = userMapper.toDto(userService.getUserById(id));
        log.info("✅ Успешно возвращен пользователь с email: {}", user.email());
        return user;
    }

    /**
     * Изменяет роль указанного пользователя.
     *
     * @param id      UUID пользователя, чья роль изменяется.
     * @param request DTO с новой ролью.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или роль не найдены.
     * @throws IllegalArgumentException                      если пытаются изменить роль администратора или присвоить уже имеющуюся роль.
     */
    @PatchMapping("/{id}/role")
    public void changeUserRole(@PathVariable UUID id,
                               @RequestBody @Valid ChangeRoleRequest request) {
        log.info("▶️ Получен запрос на изменение роли для пользователя {}. Новая роль: {}", id, request.role());
        userService.changeUserRole(id, request.role());
        log.info("✅ Роль для пользователя {} успешно изменена на {}", id, request.role());
    }

    /**
     * Деактивирует пользователя, устанавливая ему статус INACTIVE.
     *
     * @param id UUID пользователя для деактивации.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь не найден.
     * @throws IllegalArgumentException                      если пользователь уже неактивен или является администратором.
     */
    @PatchMapping("/{id}/deactivate")
    public void deactivateUser(@PathVariable UUID id) {
        log.info("▶️ Получен запрос на деактивацию пользователя: {}", id);
        userService.deactivateUser(id);
        log.info("✅ Пользователь {} успешно деактивирован", id);
    }

    /**
     * Активирует пользователя, устанавливая ему статус ACTIVE.
     *
     * @param id UUID пользователя для активации.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь не найден.
     * @throws IllegalArgumentException                      если пользователь уже активен.
     */
    @PatchMapping("/{id}/activate")
    public void activateUser(@PathVariable UUID id) {
        log.info("▶️ Получен запрос на активацию пользователя: {}", id);
        userService.activateUser(id);
        log.info("✅ Пользователь {} успешно активирован", id);
    }
}
