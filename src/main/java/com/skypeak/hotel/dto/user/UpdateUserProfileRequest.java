package com.skypeak.hotel.dto.user;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO для запроса на обновление профиля пользователя.
 * <p>
 * Позволяет изменить личные данные пользователя, такие как пароль, имя, фамилия,
 * отчество, дата рождения и аватар.
 *
 * @author Дмитрий Ельцов
 */
public record UpdateUserProfileRequest(
        @Size(message = "Пароль не может быть длиннее 255 символов", max = 255)
        String password,
        @Size(message = "Имя не может быть длиннее 50 символов", max = 50)
        String firstName,
        @Size(message = "Фамилия не может быть длиннее 50 символов", max = 50)
        String lastName,
        @Size(message = "Отчество не может быть длиннее 100 символов", max = 100)
        String middleName,
        LocalDate birthDate,
        @Size(message = "URL аватара не может быть длиннее 255 символов", max = 255)
        String avatarUrl) {
}