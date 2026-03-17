package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность роли пользователя в системе отеля.
 *
 * <p>Представляет роль доступа в таблице <code>roles</code>.
 * Служит справочником для {@link UserEntity#getRole()} и создается в {@link com.skypeak.hotel.config.DataInitializer}.</p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>ID автоинкремент (IDENTITY) вместо UUID для производительности</li>
 *   <li>Уникальное строковое имя из enum {@link Role}</li>
 *   <li>Используется в {@link com.skypeak.hotel.repository.UserRepository} через {@code @EntityGraph("role")}</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see Role
 * @see UserEntity
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

}