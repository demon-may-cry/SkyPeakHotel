package com.skypeak.hotel.dto.user;

import com.skypeak.hotel.entity.UserEntity;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 * <p>
 * DTO for {@link UserEntity}
 */
public record UserResponse(
        UUID id,
        String email,
        String status,
        String role) {
}