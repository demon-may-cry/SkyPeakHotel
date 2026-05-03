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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

import static java.text.MessageFormat.format;

/**
 * Реализация сервиса {@link BalanceService} для управления балансом пользователей.
 * <p>
 * Обеспечивает выполнение транзакционных операций пополнения, списания
 * и получения информации о счетах и транзакциях.
 *
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private final UserRepository userRepository;
    private final UserBalanceRepository balanceRepository;
    private final BalanceTransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID userId) {
        log.info("▶️ Запрос на получение баланса для пользователя {}", userId);
        return balanceRepository.findByUser_Id(userId)
                .map(UserBalanceEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public void deposit(UUID userId, BigDecimal amount, String description) {
        log.info("▶️ Запрос на пополнение баланса для пользователя {} на сумму {}. Описание: {}",
                userId, amount, description);
        updateBalance(userId, amount, description, TransactionType.DEPOSIT, BigDecimal::add);
    }

    @Override
    public void withdraw(UUID userId, BigDecimal amount, String description) {
        log.info("▶️ Запрос на списание с баланса пользователя {} на сумму {}. Описание: {}",
                userId, amount, description);
        updateBalance(userId, amount, description, TransactionType.WITHDRAW, BigDecimal::subtract);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BalanceTransactionEntity> getTransactions(UUID userId, Pageable pageable) {
        log.info("▶️ Запрос на получение истории транзакций для пользователя {}. Параметры: {}", userId, pageable);
        return transactionRepository.findByUser_Id(userId, pageable);
    }

    private void updateBalance(UUID userId,
                               BigDecimal amount,
                               String description,
                               TransactionType type,
                               BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        validateAmount(amount);

        UserEntity user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(format("Пользователь с ID {0} не найден.", userId)));

        UserBalanceEntity balance = balanceRepository.findByUser_Id(userId)
                .orElseGet(() -> createEmptyBalance(user));

        if (type == TransactionType.WITHDRAW && balance.getBalance().compareTo(amount) < 0) {
            log.error("🚫 Недостаточно средств для списания у пользователя {}. Требуется: {}, доступно: {}",
                    userId, amount, balance.getBalance());
            throw new IllegalStateException("Недостаточно средств на счете.");
        }

        balance.setBalance(operation.apply(balance.getBalance(), amount));
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);
        log.info("✅ Баланс пользователя {} обновлен. Новый баланс: {}", userId, balance.getBalance());

        saveTransaction(user, amount, type, description);
    }

    private void validateAmount(BigDecimal amount) {
        log.info("  🔎 Проверка суммы: {}", amount);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("🚫 Сумма для операции должна быть положительной. Получено: {}", amount);
            throw new IllegalArgumentException("Сумма должна быть положительным числом.");
        }
    }

    private UserBalanceEntity createEmptyBalance(UserEntity user) {
        log.info("  ➕ Создание нового пустого баланса для пользователя {}", user.getId());
        var balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(BigDecimal.ZERO);
        balance.setUpdatedAt(LocalDateTime.now());
        return balanceRepository.save(balance);
    }

    private void saveTransaction(UserEntity user,
                                 BigDecimal amount,
                                 TransactionType type,
                                 String description) {
        log.info("  ➕ Сохранение транзакции: тип={}, сумма={}, описание='{}'", type, amount, description);
        var tx = new BalanceTransactionEntity();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDescription(description);
        tx.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(tx);
        log.info("✅ Транзакция для пользователя {} успешно сохранена", user.getId());
    }

    @Override
    public BigDecimal calculateNewBalance(BigDecimal currentBalance, BalanceTransactionEntity transaction) {
        log.info("▶️ Расчет нового баланса. Текущий баланс: {}. Тип транзакции: {}, сумма: {}",
                currentBalance, transaction.getType(), transaction.getAmount());

        BigDecimal newBalance;
        if (transaction.getType() == TransactionType.DEPOSIT) {
            newBalance = currentBalance.add(transaction.getAmount());
        } else {
            newBalance = currentBalance.subtract(transaction.getAmount());
        }

        log.info("✅ Новый баланс рассчитан: {}", newBalance);
        return newBalance;
    }
}
