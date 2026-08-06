package com.skypeak.hotel.service;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.service.impl.BalanceServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для управления финансовыми операциями и балансом пользователей.
 * <p>
 * Определяет контракт для выполнения операций работы с балансом: пополнения, списания,
 * просмотра баланса и истории транзакций.
 * </p>
 *
 * <h3>Особенности работы с балансом:</h3>
 * <ul>
 *   <li>Баланс рассчитывается на основе суммы всех транзакций</li>
 *   <li>Поддержка двух типов транзакций: DEPOSIT (пополнение) и WITHDRAW (списание)</li>
 *   <li>Отрицательный баланс не допускается при списании</li>
 *   <li>Все транзакции неизменяемы (immutable) после создания для обеспечения аудита</li>
 *   <li>Каждая транзакция должна иметь описание для отслеживания источника средств</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BalanceTransactionEntity
 * @see BalanceServiceImpl
 */
public interface BalanceService {

    /**
     * Возвращает текущий баланс пользователя.
     * <p>
     * Баланс рассчитывается как сумма всех пополнений (DEPOSIT) минус сумма всех списаний (WITHDRAW).
     * </p>
     *
     * @param userId UUID пользователя
     * @return {@link BigDecimal} с текущей суммой на счету
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или его баланс не найдены
     */
    BigDecimal getBalance(UUID userId);

    /**
     * Пополняет баланс пользователя на указанную сумму.
     * <p>
     * Создает транзакцию типа DEPOSIT и увеличивает баланс пользователя.
     * Операция выполняется атомарно с обновлением баланса.
     * </p>
     *
     * @param userId      UUID пользователя
     * @param amount      Сумма пополнения (должна быть положительной)
     * @param description Описание транзакции (например, "Пополнение счета" или "Бонус за регистрацию")
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или его баланс не найдены
     * @throws IllegalArgumentException                      если сумма пополнения не является положительным числом
     */
    void deposit(UUID userId, BigDecimal amount, String description);

    /**
     * Списывает средства с баланса пользователя.
     * <p>
     * Создает транзакцию типа WITHDRAW и уменьшает баланс пользователя.
     * Операция выполняется атомарно с обновлением баланса.
     * </p>
     *
     * @param userId      UUID пользователя
     * @param amount      Сумма списания (должна быть положительной)
     * @param description Описание транзакции (например, "Оплата бронирования" или "Комиссия")
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или его баланс не найдены
     * @throws IllegalArgumentException                      если сумма списания превышает текущий баланс или не является положительным числом
     * @throws IllegalStateException                         если баланс заморожен или недоступен
     */
    void withdraw(UUID userId, BigDecimal amount, String description);

    /**
     * Возвращает пагинированную историю всех транзакций пользователя.
     * <p>
     * Транзакции возвращаются в порядке убывания даты создания (новые первыми).
     * Используется для отображения истории платежей пользователю.
     * </p>
     *
     * @param userId   UUID пользователя
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return {@link Page} с транзакциями пользователя
     */
    Page<BalanceTransactionEntity> getTransactions(UUID userId, Pageable pageable);

    /**
     * Рассчитывает новый баланс после применения транзакции.
     * <p>
     * Используется внутренне для расчета баланса на основе типа транзакции (DEPOSIT/WITHDRAW).
     * </p>
     *
     * @param currentBalance текущий баланс пользователя
     * @param transaction    транзакция для применения
     * @return {@code BigDecimal} новый баланс после применения транзакции
     */
    BigDecimal calculateNewBalance(BigDecimal currentBalance, BalanceTransactionEntity transaction);
}
