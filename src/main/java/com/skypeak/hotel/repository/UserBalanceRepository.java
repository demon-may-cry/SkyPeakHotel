package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к данным о балансах пользователей ({@link UserBalanceEntity}).
 *
 * @author Дмитрий Ельцов
 */
@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalanceEntity, UUID> {

    /**
     * Находит баланс пользователя по его уникальному идентификатору (ID).
     *
     * @param userId UUID пользователя.
     * @return {@link Optional} с {@link UserBalanceEntity}, если баланс найден, иначе {@link Optional#empty()}.
     */
    Optional<UserBalanceEntity> findByUser_Id(UUID userId);
}
