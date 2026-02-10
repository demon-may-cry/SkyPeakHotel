package com.skypeak.hotel.service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface BalanceService {

    BigDecimal getBalance(UUID userId);

    void deposit(UUID userId, BigDecimal amount, String description);

    void withdraw(UUID userId, BigDecimal amount, String description);

}
