package com.skypeak.hotel.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * DTO для запроса на регистрацию нового пользователя.
 * <p>
 * Содержит информацию, необходимую для создания новой учетной записи (email и пароль).
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class RegisterRequest {

    /**
     * Email адрес нового пользователя (уникальный идентификатор).
     * Должен быть валидным email, не пустым и иметь максимальную длину 100 символов.
     */
    @NonNull
    @Email
    @Size(max = 100)
    private String email;

    /**
     * Пароль нового пользователя.
     * Должен быть не пустым и иметь длину от 8 до 100 символов.
     */
    @NonNull
    @Size(min = 8, max = 100)
    private String password;
}
