package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.entity.UserBalanceEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.TransactionType;
import com.skypeak.hotel.repository.BalanceTransactionRepository;
import com.skypeak.hotel.repository.UserBalanceRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.BalanceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Service
@Transactional
public class BalanceServiceImpl implements BalanceService {

    private final UserRepository userRepository;
    private final UserBalanceRepository balanceRepository;
    private final BalanceTransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID userId) {
        return balanceRepository.findByUser_Id(userId)
                .map(UserBalanceEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void deposit(UUID userId, BigDecimal amount, String description) {

        validateAmount(amount);

        var user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));

        var balance = balanceRepository.findByUser_Id(userId)
                .orElseGet(() -> createEmptyBalance(user));

        balance.setBalance(balance.getBalance().add(amount));
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);

        saveTransaction(user, amount, TransactionType.DEPOSIT, description);
    }

    @Override
    public void withdraw(UUID userId, BigDecimal amount, String description) {

        validateAmount(amount);

        var balance = balanceRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("Balance not found for user"));

        if (balance.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }

        balance.setBalance(balance.getBalance().subtract(amount));
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);

        saveTransaction(balance.getUser(), amount, TransactionType.WITHDRAW, description);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BalanceTransactionEntity> getTransactions(UUID userId, Pageable pageable) {
        return transactionRepository.findByUser_Id(userId, pageable);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private UserBalanceEntity createEmptyBalance(UserEntity user) {
        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(BigDecimal.ZERO);
        balance.setUpdatedAt(LocalDateTime.now());
        return balanceRepository.save(balance);
    }

    private void saveTransaction(UserEntity user,
                                 BigDecimal amount,
                                 TransactionType type,
                                 String description) {
        BalanceTransactionEntity tx = new BalanceTransactionEntity();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(tx);
    }
}
