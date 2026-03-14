package com.skypeak.hotel.dto.user;

import jakarta.validation.constraints.NotNull;

/**
 * @author Дмитрий Ельцов
 */
public record ChangeRoleRequest(@NotNull String role) {
}
