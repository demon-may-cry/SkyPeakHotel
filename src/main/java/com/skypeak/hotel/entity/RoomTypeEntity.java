package com.skypeak.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность типа (категории) номера в отеле.
 * <p>
 * Определяет общие характеристики для группы номеров: название, базовую цену и описание.
 * Сохраняется в таблицу <code>room_types</code>.
 *
 * <p><strong>Поля и ограничения:</strong></p>
 * <ul>
 *   <li>{@link #id} - UUID, генерируется автоматически</li>
 *   <li>{@link #slug} - уникальный строковый идентификатор (standard, suite и т.д.)</li>
 *   <li>{@link #title} - отображаемое название категории</li>
 *   <li>{@link #basePrice} - базовая стоимость проживания за ночь</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoomEntity
 */
@Getter
@Setter
@Entity
@Table(name = "room_types")
public class RoomTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Тип комнаты.
     * Определяет категорию номера (STANDARD, APARTMENTS, SUITE).
     */
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    /**
     * Название типа комнаты.
     * Отображаемое название категории (например, "Стандартный номер", "Апартаменты", "Люкс").
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Описание комнаты.
     * Дополнительная информация о характеристиках номера, максимум 255 символов.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Цена комнаты за одну ночь.
     * Хранится с точностью до 2 знаков после запятой.
     */
    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    /**
     * Список всех номеров, относящихся к данному типу.
     */
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomEntity> rooms = new ArrayList<>();

}
