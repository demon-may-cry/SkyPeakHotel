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
 *
 * @author Дмитрий Ельцов
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
     */
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    /**
     * Текущая сумма на счету пользователя.
     * <p>
     * Точность: 12 знаков, 2 из которых после запятой.
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

}
