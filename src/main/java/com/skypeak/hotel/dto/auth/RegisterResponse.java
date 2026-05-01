package com.skypeak.hotel.dto.auth;

/**
 * DTO для ответа после успешной регистрации нового пользователя.
 * <p>
 * Содержит сообщение об успешной регистрации пользователя.
 *
 * @param message сообщение об успешной регистрации.
 * @author Дмитрий Ельцов
 */
public record RegisterResponse(String message) {
}
