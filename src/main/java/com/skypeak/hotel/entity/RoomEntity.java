package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

import static com.skypeak.hotel.entity.enums.RoomType.*;

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
 *   <li>{@link #roomType} - тип комнаты ({@link RoomType})</li>
 *   <li>{@link #pricePerNight} - цена за ночь</li>
 *   <li>{@link #active} - флаг доступности для бронирования</li>
 *   <li>{@link #description} - описание комнаты, до 255 символов</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoomType
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
     * Тип комнаты.
     * Определяет категорию номера (STANDARD, APARTMENTS, SUITE).
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "room_type", nullable = false, length = 30)
    private RoomType roomType;

    /**
     * Цена комнаты за одну ночь.
     * Хранится с точностью до 2 знаков после запятой.
     */
    @NotNull
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    /**
     * Флаг активности комнаты.
     * Определяет, доступна ли комната для бронирования.
     */
    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

    /**
     * Описание комнаты.
     * Дополнительная информация о характеристиках номера, максимум 255 символов.
     */
    @Size(max = 255)
    @Column(name = "description")
    private String description;

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
     * Возвращает описание типа комнаты.
     *
     * @return строковое описание типа комнаты
     */
    @SuppressWarnings("unused")
    public String getRoomTypeDescription() {
        return switch (roomType) {
            case STANDARD -> "Стандартный номер с базовым набором удобств";
            case APARTMENTS -> "Апартаменты с расширенным набором удобств";
            case SUITE -> "Люкс с премиум уровнем комфорта";
        };
    }

    /**
     * Возвращает отображаемое название комнаты.
     * Формат: "Номер [roomNumber] - [тип комнаты]"
     *
     * @return форматированное название комнаты
     */
    @SuppressWarnings("unused")
    public String getDisplayName() {
        return "Номер " + roomNumber + " - " + getRoomTypeDescription();
    }

    /**
     * Возвращает полное описание комнаты.
     * Включает номер, тип, цену и дополнительное описание.
     *
     * @return полное описание комнаты
     */
    @SuppressWarnings("unused")
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Номер ").append(roomNumber).append(" - ").append(getRoomTypeDescription());
        sb.append("\nЦена за ночь: ").append(getFormattedPrice());
        if (description != null && !description.trim().isEmpty()) {
            sb.append("\nОписание: ").append(description);
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
        return pricePerNight + " ₽";
    }

    /**
     * Проверяет, является ли комната стандартного типа.
     *
     * @return true если тип комнаты STANDARD
     */
    @SuppressWarnings("unused")
    public boolean isStandard() {
        return roomType == STANDARD;
    }

    /**
     * Проверяет, является ли комната апартаментами.
     *
     * @return true если тип комнаты APARTMENTS
     */
    @SuppressWarnings("unused")
    public boolean isApartments() {
        return roomType == APARTMENTS;
    }

    /**
     * Проверяет, является ли комната люксом.
     *
     * @return true если тип комнаты SUITE
     */
    @SuppressWarnings("unused")
    public boolean isSuite() {
        return roomType == SUITE;
    }

}