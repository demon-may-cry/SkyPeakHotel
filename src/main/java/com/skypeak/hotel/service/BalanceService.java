package com.skypeak.hotel.service;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface BalanceService {

    BigDecimal getBalance(UUID userId);

    void deposit(UUID userId, BigDecimal amount, String description);

    void withdraw(UUID userId, BigDecimal amount, String description);

    Page<BalanceTransactionEntity> getTransactions(UUID userId, Pageable pageable);
}
