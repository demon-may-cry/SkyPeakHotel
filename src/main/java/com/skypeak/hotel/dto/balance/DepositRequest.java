package com.skypeak.hotel.dto.balance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO для запроса на пополнение баланса пользователя.
 * <p>
 * Содержит сумму для пополнения счета пользователя.
 *
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class DepositRequest {

    /**
     * Сумма пополнения счета.
     * Не должна быть пустой/null и должна быть положительной.
     */
    @NotNull
    @Positive
    private BigDecimal amount;
}
