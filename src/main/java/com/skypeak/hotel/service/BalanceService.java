package com.skypeak.hotel.service;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для управления балансом пользователей.
 * <p>
 * Определяет контракт для выполнения операций со счетом пользователя,
 * таких как получение баланса, пополнение, списание и просмотр истории транзакций.
 *
 * @author Дмитрий Ельцов
 * @see com.skypeak.hotel.service.impl.BalanceServiceImpl
 */
public interface BalanceService {

    /**
     * Возвращает текущий баланс пользователя.
     *
     * @param userId UUID пользователя.
     * @return {@link BigDecimal} с текущей суммой на счету.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или его баланс не найдены.
     */
    BigDecimal getBalance(UUID userId);

    /**
     * Пополняет баланс пользователя на указанную сумму с заданным описанием.
     *
     * @param userId      UUID пользователя.
     * @param amount      Сумма пополнения. Должна быть положительной.
     * @param description Описание транзакции (например, "Пополнение счета" или "Бонус за регистрацию").
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или его баланс не найдены.
     * @throws IllegalArgumentException                      если сумма пополнения не является положительным числом.
     */
    void deposit(UUID userId, BigDecimal amount, String description);

    /**
     * Списывает средства с баланса пользователя.
     *
     * @param userId      UUID пользователя.
     * @param amount      Сумма списания. Должна быть положительной.
     * @param description Описание транзакции (например, "Оплата заказа #123").
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или его баланс не найдены.
     * @throws IllegalArgumentException                      если сумма списания превышает текущий баланс или не является положительным числом.
     */
    void withdraw(UUID userId, BigDecimal amount, String description);

    /**
     * Возвращает пагинированную историю транзакций пользователя.
     *
     * @param userId   UUID пользователя.
     * @param pageable параметры пагинации.
     * @return {@link Page} с {@link BalanceTransactionEntity}.
     */
    Page<BalanceTransactionEntity> getTransactions(UUID userId, Pageable pageable);
}
