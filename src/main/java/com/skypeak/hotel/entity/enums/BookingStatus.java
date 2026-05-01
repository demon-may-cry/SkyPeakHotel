package com.skypeak.hotel.entity.enums;

/**
 * Статусы бронирования номера в отеле.
 * <p>
 * Определяет жизненный цикл бронирования от создания до завершения:
 * </p>
 * <ul>
 *   <li>{@link #CREATED} - бронирование создано, ожидает оплаты или подтверждения</li>
 *   <li>{@link #CANCELLED} - бронирование отменено пользователем или администратором</li>
 *   <li>{@link #COMPLETED} - проживание завершено, бронирование закрыто</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see com.skypeak.hotel.entity.BookingEntity
 */
public enum BookingStatus {
    CREATED,
    CANCELLED,
    COMPLETED
}
