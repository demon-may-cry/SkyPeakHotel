package com.skypeak.hotel.dto.user;

import com.skypeak.hotel.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

/**
 * @author Дмитрий Ельцов
 */
public record ChangeRoleRequest(@NotNull Role role) {
}
