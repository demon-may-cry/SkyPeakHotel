package com.skypeak.hotel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность, представляющая баланс пользователя.
 * <p>
 * Хранит текущую сумму на счету пользователя и дату последнего обновления.
 * Связана с {@link UserEntity} отношением "один к одному".
 * </p>
 *
 * <h3>Расчет баланса:</h3>
 * <ul>
 *   <li>Баланс рассчитывается как сумма всех {@link BalanceTransactionEntity} типа DEPOSIT минус сумма всех WITHDRAW</li>
 *   <li>Обновляется атомарно при каждой финансовой операции</li>
 *   <li>Используется для проверки достаточности средств при бронировании</li>
 * </ul>
 *
 * <h3>Бизнес-правила:</h3>
 * <ul>
 *   <li>Баланс не может быть отрицательным при списании средств</li>
 *   <li>Пополнение баланса возможно без ограничений</li>
 *   <li>Баланс блокируется во время проведения транзакций для обеспечения консистентности</li>
 * </ul>
 *
 * <h3>Связь с транзакциями:</h3>
 * <ul>
 *   <li>Каждая транзакция обновляет поле {@link #updatedAt}</li>
 *   <li>История всех операций хранится в {@link BalanceTransactionEntity}</li>
 *   <li>Баланс является кэшированным результатом агрегации транзакций</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 * @see BalanceTransactionEntity
 * @see com.skypeak.hotel.service.BalanceService
 */
@Getter
@Setter
@Entity
@Table(name = "user_balances")
public class UserBalanceEntity {

    /**
     * Уникальный идентификатор записи о балансе.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Пользователь, которому принадлежит этот баланс.
     * <p>
     * Связь "один к одному" с {@link UserEntity}. Загружается лениво.
     * </p>
     */
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    /**
     * Текущая сумма на счету пользователя.
     * <p>
     * Точность: 12 знаков, 2 из которых после запятой.
     * </p>
     */
    @NotNull
    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    /**
     * Дата и время последнего обновления баланса.
     */
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Проверяет, достаточно ли средств на балансе для указанной суммы.
     *
     * @param amount сумма для проверки
     * @return true если баланс >= указанной суммы
     */
    @SuppressWarnings("unused")
    public boolean hasEnoughFunds(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    /**
     * Проверяет, является ли баланс положительным.
     *
     * @return true если баланс > 0
     */
    @SuppressWarnings("unused")
    public boolean isPositive() {
        return balance.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Проверяет, является ли баланс нулевым.
     *
     * @return true если баланс == 0
     */
    @SuppressWarnings("unused")
    public boolean isZero() {
        return balance.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Проверяет, является ли баланс отрицательным.
     *
     * @return true если баланс < 0
     */
    @SuppressWarnings("unused")
    public boolean isNegative() {
        return balance.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Возвращает баланс в формате строки с символом валюты.
     *
     * @return форматированная строка баланса
     */
    @SuppressWarnings("unused")
    public String getFormattedBalance() {
        return balance + " ₽";
    }

    /**
     * Вычисляет новый баланс после применения транзакции.
     *
     * @param transaction транзакция для применения
     * @return новый баланс после применения транзакции
     */
    @SuppressWarnings("unused")
    public BigDecimal calculateNewBalance(BalanceTransactionEntity transaction) {
        return balance.add(transaction.getAmount().multiply(BigDecimal.valueOf(transaction.getSign())));
    }
}
