package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Сущность пользователя системы отеля.
 *
 * <p>Представляет учетную запись пользователя с уникальным email, закодированным паролем,
 * статусом и ролью доступа. Сохраняется в таблицу <code>users</code>.</p>
 *
 * <p><strong>Поля и ограничения:</strong></p>
 * <ul>
 *   <li>{@link #id} - UUID, генерируется автоматически</li>
 *   <li>{@link #email} - уникальный, до 100 символов, обязательное</li>
 *   <li>{@link #password} - закодированный (BCrypt), до 255 символов, обязательное</li>
 *   <li>{@link #status} - {@link Status} (ACTIVE/INACTIVE/BLOCKED)</li>
 *   <li>{@link #role} - связь с {@link RoleEntity} (ADMIN/MANAGER/USER), lazy loading</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoleEntity
 * @see Status
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    /**
     * Уникальный идентификатор пользователя (UUID).
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Уникальный email пользователя.
     * Максимум 100 символов, индексируется как уникальное поле.
     */
    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    /**
     * Закодированный пароль (BCrypt через PasswordEncoder).
     * Хранится в хэшированном виде, максимум 255 символов.
     */
    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Статус учетной записи пользователя.
     * Определяет доступные действия (см. {@link Status}).
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status", nullable = false, length = 50)
    private Status status;

    /**
     * Роль пользователя в системе.
     * Lazy загрузка для избежания N+1 проблемы при выборке пользователей.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

}