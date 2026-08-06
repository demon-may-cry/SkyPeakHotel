package com.skypeak.hotel.dto.user;

import com.skypeak.hotel.entity.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для представления данных пользователя в ответах API.
 * <p>
 * Содержит основную информацию об пользователе: уникальный идентификатор, email, статус и роль.
 * Используется для трансформации сущности {@link UserEntity} в JSON для клиента.
 *
 * @param id     уникальный идентификатор пользователя (UUID).
 * @param email  email адрес пользователя.
 * @param status статус пользователя (ACTIVE или INACTIVE).
 * @param role   роль пользователя в системе (USER, MANAGER или ADMIN).
 * @author Дмитрий Ельцов
 * @see UserEntity
 */
public record UserResponse(
        UUID id,
        String phoneNumber,
        String email,
        String status,
        String role,
        String firstName,
        String lastName,
        String middleName,
        LocalDate birthDate,
        String avatarUrl,
        LocalDateTime createdAt,
        LocalDateTime lastLoginAt) {
}