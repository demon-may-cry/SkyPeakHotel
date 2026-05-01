package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.skypeak.hotel.entity.enums.TransactionType.*;

/**
 * Сущность, представляющая финансовую транзакцию по балансу пользователя.
 * <p>
 * Каждая запись в этой таблице — это либо пополнение (DEPOSIT), либо списание (WITHDRAW).
 * Обеспечивает полный аудит всех финансовых операций в системе.
 * </p>
 *
 * <h3>Бизнес-логика транзакций:</h3>
 * <ul>
 *   <li><strong>DEPOSIT:</strong> положительная сумма увеличивает баланс пользователя</li>
 *   <li><strong>WITHDRAW:</strong> положительная сумма уменьшает баланс пользователя</li>
 *   <li>Все транзакции неизменяемы после создания (immutable)</li>
 *   <li>Каждая транзакция должна иметь описание для аудита</li>
 * </ul>
 *
 * <h3>Связь с балансом пользователя:</h3>
 * <ul>
 *   <li>Транзакции используются для расчета текущего баланса в {@link UserBalanceEntity}</li>
 *   <li>Сумма всех DEPOSIT минус сумма всех WITHDRAW = текущий баланс</li>
 *   <li>Транзакции создаются атомарно с обновлением баланса</li>
 * </ul>
 *
 * <h3>Аудит и безопасность:</h3>
 * <ul>
 *   <li>Временная метка {@link #createdAt} используется для хронологии операций</li>
 *   <li>Описание {@link #description} обязательно для отслеживания источника средств</li>
 *   <li>Связь с пользователем обеспечивает персонализацию истории транзакций</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 * @see UserBalanceEntity
 * @see TransactionType
 * @see com.skypeak.hotel.service.BalanceService
 */
@Getter
@Setter
@Entity
@Table(name = "balance_transactions")
public class BalanceTransactionEntity {

    /**
     * Уникальный идентификатор транзакции.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Пользователь, совершивший транзакцию.
     * <p>
     * Связь "многие к одному" с {@link UserEntity}.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Сумма транзакции.
     */
    @NotNull
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /**
     * Тип транзакции (DEPOSIT или WITHDRAW).
     * <p>
     * Хранится в базе данных как строка.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    /**
     * Описание транзакции, например, "Пополнение счета" или "Оплата заказа".
     */
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    /**
     * Дата и время создания транзакции.
     */
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Проверяет, является ли транзакция пополнением счета.
     *
     * @return true если тип транзакции DEPOSIT
     */
    @SuppressWarnings("unused")
    public boolean isDeposit() {
        return type == DEPOSIT;
    }

    /**
     * Проверяет, является ли транзакция списанием со счета.
     *
     * @return true если тип транзакции WITHDRAW
     */
    @SuppressWarnings("unused")
    public boolean isWithdraw() {
        return type == WITHDRAW;
    }

    /**
     * Возвращает знак транзакции для математических операций.
     *
     * @return 1 для DEPOSIT, -1 для WITHDRAW
     */
    @SuppressWarnings("unused")
    public int getSign() {
        return isDeposit() ? 1 : -1;
    }

    /**
     * Возвращает форматированное описание транзакции.
     * Если описание не задано, возвращает стандартное описание на основе типа.
     *
     * @return описание транзакции
     */
    @SuppressWarnings("unused")
    public String getFormattedDescription() {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        return isDeposit() ? "Пополнение счета" : "Списание со счета";
    }

    /**
     * Возвращает абсолютную сумму транзакции для отображения.
     *
     * @return положительная сумма транзакции
     */
    @SuppressWarnings("unused")
    public BigDecimal getDisplayAmount() {
        return amount.abs();
    }

}
