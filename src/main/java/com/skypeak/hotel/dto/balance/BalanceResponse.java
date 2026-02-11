package com.skypeak.hotel.dto.balance;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
public record BalanceResponse(BigDecimal balance) {
}