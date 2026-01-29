package com.skypeak.hotel.dto.error;

import java.time.LocalDateTime;

/**
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
