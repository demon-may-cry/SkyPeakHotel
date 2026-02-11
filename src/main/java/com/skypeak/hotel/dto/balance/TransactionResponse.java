package com.skypeak.hotel.dto.balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public record TransactionResponse(
        UUID id,
        BigDecimal amount,
        String type,
        String description,
        LocalDateTime createdAt
) {
}
