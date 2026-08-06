package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.skypeak.hotel.entity.enums.Role.*;

/**
 * Сущность роли пользователя в системе отеля.
 * <p>
 * Представляет роль доступа в таблице <code>roles</code>.
 * Служит справочником для {@link UserEntity#getRole()} и создается в {@link com.skypeak.hotel.config.DataInitializer}.
 * </p>
 *
 * <h3>Ролевая модель системы:</h3>
 * <ul>
 *   <li><strong>USER:</strong> обычный клиент - бронирование номеров, просмотр профиля, управление балансом</li>
 *   <li><strong>MANAGER:</strong> менеджер - управление пользователями, номерами, бронированиями, балансами пользователей</li>
 *   <li><strong>ADMIN:</strong> администратор - полный доступ ко всем функциям системы</li>
 * </ul>
 *
 * <h3>Интеграция с Spring Security:</h3>
 * <ul>
 *   <li>Используется в {@link org.springframework.security.core.GrantedAuthority}</li>
 *   <li>Проверяется через {@link org.springframework.security.access.prepost.PreAuthorize}</li>
 *   <li>Загружается через {@link com.skypeak.hotel.security.CustomUserDetailsService}</li>
 * </ul>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>ID автоинкремент (IDENTITY) вместо UUID для производительности</li>
 *   <li>Уникальное строковое имя из enum {@link Role}</li>
 *   <li>Используется в {@link com.skypeak.hotel.repository.UserRepository} через {@code @EntityGraph("role")}</li>
 *   <li>Кэшируется для оптимизации производительности</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see Role
 * @see UserEntity
 * @see com.skypeak.hotel.config.DataInitializer
 * @see com.skypeak.hotel.security.CustomUserDetailsService
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class RoleEntity {
    /**
     * Автоинкрементный первичный ключ.
     * Использует IDENTITY стратегию для высокой производительности при создании ролей.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * Название роли из enum {@link Role}.
     * Хранится строкой (ADMIN, MANAGER, USER) с уникальным индексом.
     * Используется для определения прав доступа пользователя.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "name", nullable = false, length = 30, unique = true)
    private Role name;

    /**
     * Возвращает описание роли на основе ее типа.
     *
     * @return строковое описание роли
     */
    @SuppressWarnings("unused")
    public String getDescription() {
        return switch (name) {
            case ADMIN -> "Администратор - полный доступ ко всем функциям системы";
            case MANAGER -> "Менеджер - управление пользователями, номерами, бронированиями, балансами пользователей";
            case USER -> "Пользователь - бронирование номеров, просмотр профиля, управление балансом";
        };
    }

    /**
     * Проверяет, имеет ли роль административные права.
     *
     * @return true для ADMIN и MANAGER ролей
     */
    @SuppressWarnings("unused")
    public boolean hasAdminRights() {
        return name == ADMIN || name == MANAGER;
    }

    /**
     * Проверяет, имеет ли роль права на управление пользователями.
     *
     * @return true для ADMIN и MANAGER ролей
     */
    @SuppressWarnings("unused")
    public boolean canManageUsers() {
        return name == ADMIN || name == MANAGER;
    }

    /**
     * Проверяет, имеет ли роль права на управление номерами.
     *
     * @return true для ADMIN и MANAGER ролей
     */
    @SuppressWarnings("unused")
    public boolean canManageRooms() {
        return name == ADMIN || name == MANAGER;
    }

}