package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для доступа к данным о транзакциях по балансу ({@link BalanceTransactionEntity}).
 *
 * @author Дмитрий Ельцов
 */
@Repository
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, UUID> {

    /**
     * Находит пагинированный список транзакций для указанного пользователя.
     *
     * @param userId   UUID пользователя, чьи транзакции нужно найти.
     * @param pageable параметры пагинации (номер страницы, размер, сортировка).
     * @return {@link Page} с транзакциями пользователя.
     */
    Page<BalanceTransactionEntity> findByUser_Id(UUID userId, Pageable pageable);
}
