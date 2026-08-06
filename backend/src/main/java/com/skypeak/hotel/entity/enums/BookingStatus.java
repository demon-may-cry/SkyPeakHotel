package com.skypeak.hotel.entity.enums;

/**
 * Статусы бронирования номера в отеле.
 * <p>
 * Определяет жизненный цикл бронирования от создания до завершения:
 * </p>
 * <ul>
 *   <li>{@link #PENDING} - бронирование создано и ожидает подтверждения</li>
 *   <li>{@link #CONFIRMED} - бронирование подтверждено</li>
 *   <li>{@link #CHECKED_IN} - гость заселился</li>
 *   <li>{@link #CHECKED_OUT} - проживание завершено</li>
 *   <li>{@link #CANCELLED} - бронирование отменено</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see com.skypeak.hotel.entity.BookingEntity
 */
public enum BookingStatus {
    /**
     * Ожидает подтверждения.
     */
    PENDING,
    /**
     * Подтверждено менеджером.
     */
    CONFIRMED,
    /**
     * Гость заселился.
     */
    CHECKED_IN,
    /**
     * Проживание завершено.
     */
    CHECKED_OUT,
    /**
     * Бронирование отменено.
     */
    CANCELLED
}
