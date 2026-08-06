package com.skypeak.hotel.service;

import com.skypeak.hotel.entity.RoomTypeEntity;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Сервис для управления типами (категориями) номеров отеля.
 * <p>
 * Предоставляет методы для получения информации о доступных категориях проживания,
 * таких как стандарт, люкс или апартаменты.
 * </p>
 *
 * @author Дмитрий Ельцов
 * @see RoomTypeEntity
 */
public interface RoomTypeService {

    /**
     * Возвращает список всех существующих типов номеров.
     *
     * @return список сущностей {@link RoomTypeEntity}
     */
    List<RoomTypeEntity> getAllRoomTypes();

    /**
     * Находит тип номера по его уникальному строковому идентификатору (slug).
     *
     * @param slug строковый идентификатор типа (например, "standard")
     * @return сущность {@link RoomTypeEntity}
     * @throws EntityNotFoundException если тип с таким slug не найден
     */
    RoomTypeEntity getBySlug(String slug);
}
