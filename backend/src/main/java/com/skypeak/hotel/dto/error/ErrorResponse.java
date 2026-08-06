package com.skypeak.hotel.dto.error;

import java.time.LocalDateTime;

/**
 * DTO для представления ошибок в ответах API.
 * <p>
 * Содержит детальную информацию об ошибке, которая произошла при обработке запроса.
 * Используется для единообразного представления ошибок клиентов.
 *
 * @param timestamp дата и время возникновения ошибки.
 * @param status    HTTP статус код ошибки.
 * @param error     тип/название ошибки.
 * @param message   описание ошибки.
 * @param path      путь к эндпоинту, где произошла ошибка.
 * @author Дмитрий Ельцов
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
){
}
