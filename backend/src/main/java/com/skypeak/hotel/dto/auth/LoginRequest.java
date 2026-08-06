package com.skypeak.hotel.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO для запроса на авторизацию пользователя.
 * <p>
 * Содержит учетные данные пользователя (email и пароль) для входа в систему.
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    /**
     * Email адрес пользователя (уникальный идентификатор).
     * Должен быть валидным email и не должен быть пустым.
     */
    @NotBlank
    @Email
    private String email;

    /**
     * Пароль пользователя.
     * Не должен быть пустым.
     */
    @NotBlank
    private String password;
}
