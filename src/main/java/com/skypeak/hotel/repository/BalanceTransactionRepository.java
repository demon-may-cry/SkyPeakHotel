package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.service.BalanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для доступа к данным о транзакциях по балансу ({@link BalanceTransactionEntity}).
 * <p>
 * Предоставляет методы для поиска и пагинации транзакций пользователей.
 * Все транзакции хранятся в неизменяемом виде для обеспечения аудита финансовых операций.
 * </p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Транзакции упорядочены по дате создания (createdAt) в убывающем порядке</li>
 *   <li>Поддержка пагинации для больших объемов данных</li>
 *   <li>Связь с пользователем через user_id для персонализации истории</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BalanceTransactionEntity
 * @see BalanceService
 */
@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, UUID> {

    /**
     * Находит пагинированный список транзакций для указанного пользователя.
     * <p>
     * Транзакции возвращаются в порядке убывания даты создания (новые первыми).
     * Используется для отображения истории операций пользователя.
     * </p>
     *
     * @param userId   UUID пользователя, чьи транзакции нужно найти
     * @param pageable параметры пагинации (номер страницы, размер, сортировка)
     * @return {@link Page} с транзакциями пользователя
     */
    Page<BalanceTransactionEntity> findByUser_Id(UUID userId, Pageable pageable);
}
