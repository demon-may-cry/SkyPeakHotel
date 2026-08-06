package com.skypeak.hotel.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * DTO для представления текущего баланса пользователя в ответах API.
 * <p>
 * Содержит текущий баланс счета пользователя в системе.
 *
 * @param balance текущий баланс счета пользователя.
 * @author Дмитрий Ельцов
 */
public record BalanceResponse(@JsonProperty("balance") BigDecimal balance) {
}