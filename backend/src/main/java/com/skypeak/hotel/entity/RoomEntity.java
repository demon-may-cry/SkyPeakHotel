package com.skypeak.hotel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Сущность комнаты в отеле.
 * <p>
 * Представляет информацию о номере в отеле с его характеристиками и ценой.
 * Сохраняется в таблицу <code>rooms</code>.
 *
 * <p><strong>Поля и ограничения:</strong></p>
 * <ul>
 *   <li>{@link #id} - UUID, генерируется автоматически</li>
 *   <li>{@link #roomNumber} - уникальный номер комнаты, до 20 символов</li>
 *   <li>{@link #roomType} - тип комнаты ({@link RoomTypeEntity})</li>
 *   <li>{@link #active} - флаг доступности для бронирования</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoomTypeEntity
 */
@Getter
@Setter
@Entity
@Table(name = "rooms")
public class RoomEntity {
    /**
     * Уникальный идентификатор комнаты (UUID).
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Номер комнаты в отеле.
     * Уникальный идентификатор комнаты, максимум 20 символов.
     */
    @Size(max = 20)
    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    /**
     * Флаг активности комнаты.
     * Определяет, доступна ли комната для бронирования.
     */
    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomTypeEntity roomType;

    /**
     * Проверяет, доступна ли комната для бронирования.
     * Комната доступна если она активна.
     *
     * @return true если комната доступна для бронирования
     */
    @SuppressWarnings("unused")
    public boolean isAvailable() {
        return active;
    }

    /**
     * Использует заголовок из сущности типа комнаты.
     *
     * @return строковое описание типа комнаты
     */
    @SuppressWarnings("unused")
    public String getRoomTitle() {
        return roomType.getTitle();
    }

    /**
     * Возвращает отображаемое название комнаты.
     * Формат: "Номер [roomNumber] - [тип комнаты]"
     *
     * @return форматированное название комнаты
     */
    @SuppressWarnings("unused")
    public String getDisplayName() {
        return "Номер " + roomNumber + " - " + roomType.getTitle();
    }

    /**
     * Возвращает полное описание комнаты.
     * Включает номер, тип, цену и расширенное описание из категории.
     *
     * @return полное описание комнаты
     */
    @SuppressWarnings("unused")
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Номер ").append(roomNumber).append(" - ").append(roomType.getTitle());
        sb.append("\nЦена за ночь: ").append(getFormattedPrice());
        if (roomType.getDescription() != null && !roomType.getDescription().trim().isEmpty()) {
            sb.append("\n\n").append(roomType.getDescription());
        }
        return sb.toString();
    }

    /**
     * Возвращает цену в формате строки с символом валюты.
     *
     * @return форматированная цена
     */
    @SuppressWarnings("unused")
    public String getFormattedPrice() {
        return roomType.getBasePrice().toString() + " ₽";
    }

    /**
     * Проверяет, является ли комната стандартного типа.
     *
     * @return true если тип комнаты STANDARD
     */
    @SuppressWarnings("unused")
    public boolean isStandard() {
        return "standard".equals(roomType.getSlug());
    }

    /**
     * Проверяет, является ли комната апартаментами.
     *
     * @return true если тип комнаты APARTMENTS
     */
    @SuppressWarnings("unused")
    public boolean isApartments() {
        return "apartments".equals(roomType.getSlug());
    }

    /**
     * Проверяет, является ли комната люксом.
     *
     * @return true если тип комнаты SUITE
     */
    @SuppressWarnings("unused")
    public boolean isSuite() {
        return "suite".equals(roomType.getSlug());
    }

}