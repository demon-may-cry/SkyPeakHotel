package com.skypeak.hotel.entity.enums;

/**
 * Типы номеров в отеле.
 * <p>
 * Определяет категории номеров по уровню комфорта и цене:
 * </p>
 * <ul>
 *   <li>{@link #STANDARD} - стандартный номер с базовым набором удобств</li>
 *   <li>{@link #APARTMENTS} - апартаменты с расширенным набором удобств</li>
 *   <li>{@link #SUITE} - люкс с премиум уровнем комфорта</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see com.skypeak.hotel.entity.RoomEntity
 */
public enum RoomType {
    STANDARD,
    APARTMENTS,
    SUITE
}
