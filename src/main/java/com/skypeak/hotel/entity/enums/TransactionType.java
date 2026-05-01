package com.skypeak.hotel.entity.enums;

/**
 * Типы финансовых транзакций по балансу пользователя.
 * <p>
 * Определяет направление движения средств:
 * </p>
 * <ul>
 *   <li>{@link #DEPOSIT} - пополнение счета (увеличение баланса)</li>
 *   <li>{@link #WITHDRAW} - списание со счета (уменьшение баланса)</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see com.skypeak.hotel.entity.BalanceTransactionEntity
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAW
}
