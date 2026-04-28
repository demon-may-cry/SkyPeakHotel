package com.skypeak.hotel.dto.balance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
public record BalanceResponse(@JsonProperty("balance") BigDecimal balance) {
}