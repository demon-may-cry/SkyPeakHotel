package com.skypeak.hotel.entity.enums;

/**
 * Роли пользователей в системе отеля.
 *
 * <p>Определяет уровни доступа и привилегии:</p>
 *
 * <ul>
 *   <li>{@link #ADMIN} - полный доступ ко всем функциям</li>
 *   <li>{@link #MANAGER} - управление пользователями и заказами</li>
 *   <li>{@link #USER} - обычный клиент отеля</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see com.skypeak.hotel.entity.RoleEntity
 * @see com.skypeak.hotel.config.DataInitializer
 */
public enum Role {
    ADMIN,
    MANAGER,
    USER
}
