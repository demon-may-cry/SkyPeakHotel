package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к данным о типах номеров ({@link RoomTypeEntity}).
 * <p>
 * Предоставляет методы для поиска категорий номеров по их уникальным слаг-идентификаторам.
 * </p>
 *
 * @author Дмитрий Ельцов
 * @see RoomTypeEntity
 */
@Repository
public interface RoomTypeEntityRepository extends JpaRepository<RoomTypeEntity, UUID> {

    /**
     * Находит тип комнаты по его слагу (уникальному строковому идентификатору).
     *
     * @param slug строковый идентификатор типа (например, 'standard', 'suite')
     * @return {@link Optional} с {@link RoomTypeEntity}, если тип найден, иначе {@link Optional#empty()}
     */
    Optional<RoomTypeEntity> findBySlug(String slug);
}
