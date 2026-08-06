package com.skypeak.hotel.dto.user;

import com.skypeak.hotel.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для запроса на изменение роли пользователя.
 * <p>
 * Содержит новую роль, которую необходимо присвоить пользователю.
 *
 * @param role новая роль для пользователя (USER, MANAGER или ADMIN). Не должна быть пустой.
 * @author Дмитрий Ельцов
 */
public record ChangeRoleRequest(@NotNull Role role) {
}
