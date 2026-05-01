package com.skypeak.hotel.dto.auth;

/**
 * DTO для ответа после успешной авторизации пользователя.
 * <p>
 * Содержит JWT токен доступа и тип токена для использования при последующих запросах.
 *
 * @param accessToken токен доступа (JWT) для аутентификации в защищенных эндпоинтах.
 * @param tokenType   тип токена (обычно "Bearer").
 * @author Дмитрий Ельцов
 */
public record LoginResponse(String accessToken, String tokenType) {

}
