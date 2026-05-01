package com.skypeak.hotel.dto.balance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для представления информации о транзакции баланса в ответах API.
 * <p>
 * Содержит полную информацию о транзакции пополнения или списания со счета пользователя.
 *
 * @param id          уникальный идентификатор транзакции (UUID).
 * @param amount      сумма транзакции.
 * @param type        тип транзакции (DEPOSIT, CHARGE и т.д.).
 * @param description описание причины транзакции.
 * @param createdAt   дата и время создания транзакции.
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
