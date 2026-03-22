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

/**
 * Сущность, представляющая финансовую транзакцию по балансу пользователя.
 * <p>
 * Каждая запись в этой таблице — это либо пополнение (DEPOSIT), либо списание (WITHDRAW).
 *
 * @author Дмитрий Ельцов
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

}
