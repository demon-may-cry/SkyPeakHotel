package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserBalanceEntity;
import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.service.BalanceService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к данным о балансах пользователей ({@link UserBalanceEntity}).
 * <p>
 * Предоставляет методы для поиска балансов пользователей. Каждый пользователь имеет
 * ровно один баланс, который обновляется при каждой транзакции.
 * </p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Один пользователь - один баланс (1:1 отношение)</li>
 *   <li>Баланс рассчитывается на основе суммы всех транзакций пользователя</li>
 *   <li>Баланс обновляется атомарно с созданием транзакций</li>
 *   <li>Отрицательный баланс не допускается бизнес-логикой</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserBalanceEntity
 * @see BalanceTransactionEntity
 * @see BalanceService
 */
@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalanceEntity, UUID> {

    /**
     * Находит баланс пользователя по его уникальному идентификатору (ID).
     * <p>
     * Используется для получения текущего баланса пользователя перед операциями
     * пополнения или списания средств.
     * </p>
     *
     * @param userId UUID пользователя
     * @return {@link Optional} с {@link UserBalanceEntity}, если баланс найден, иначе {@link Optional#empty()}
     */
    Optional<UserBalanceEntity> findByUser_Id(UUID userId);
}
