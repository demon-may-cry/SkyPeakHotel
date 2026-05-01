package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import static com.skypeak.hotel.entity.enums.Role.*;
import static com.skypeak.hotel.entity.enums.Status.*;

/**
 * Сущность пользователя системы отеля.
 * <p>
 * Представляет учетную запись пользователя с уникальным email, закодированным паролем,
 * статусом и ролью доступа. Сохраняется в таблицу <code>users</code>.
 * </p>
 *
 * <h3>Бизнес-логика:</h3>
 * <ul>
 *   <li>Уникальность email обеспечивает невозможность дублирования учетных записей</li>
 *   <li>Пароль хранится в закодированном виде с использованием BCrypt</li>
 *   <li>Статус определяет возможность авторизации и выполнения операций</li>
 *   <li>Роль определяет уровень доступа к функционалу системы</li>
 * </ul>
 *
 * <h3>Связи с другими сущностями:</h3>
 * <ul>
 *   <li><strong>RoleEntity:</strong> "многие к одному" - каждый пользователь имеет одну роль</li>
 *   <li><strong>BookingEntity:</strong> "один ко многим" - пользователь может иметь несколько бронирований</li>
 *   <li><strong>UserBalanceEntity:</strong> "один к одному" - каждый пользователь имеет один баланс</li>
 *   <li><strong>BalanceTransactionEntity:</strong> "один ко многим" - пользователь может иметь несколько транзакций</li>
 * </ul>
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
 * @see BookingEntity
 * @see UserBalanceEntity
 * @see BalanceTransactionEntity
 * @see com.skypeak.hotel.repository.UserRepository
 * @see com.skypeak.hotel.service.UserService
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

    /**
     * Проверяет, имеет ли пользователь указанную роль.
     *
     * @param role роль для проверки
     * @return true если пользователь имеет указанную роль
     */
    @SuppressWarnings("unused")
    public boolean hasRole(Role role) {
        return this.role != null && this.role.getName() == role;
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @return true если пользователь имеет роль ADMIN
     */
    @SuppressWarnings("unused")
    public boolean isAdmin() {
        return hasRole(ADMIN);
    }

    /**
     * Проверяет, является ли пользователь менеджером.
     *
     * @return true если пользователь имеет роль MANAGER
     */
    @SuppressWarnings("unused")
    public boolean isManager() {
        return hasRole(MANAGER);
    }

    /**
     * Проверяет, является ли пользователь обычным пользователем.
     *
     * @return true если пользователь имеет роль USER
     */
    @SuppressWarnings("unused")
    public boolean isUser() {
        return hasRole(USER);
    }

    /**
     * Проверяет, активен ли аккаунт пользователя.
     *
     * @return true если статус пользователя ACTIVE
     */
    @SuppressWarnings("unused")
    public boolean isActive() {
        return this.status == ACTIVE;
    }

    /**
     * Проверяет, заблокирован ли аккаунт пользователя.
     *
     * @return true если статус пользователя BLOCKED
     */
    @SuppressWarnings("unused")
    public boolean isBlocked() {
        return this.status == BLOCKED;
    }

}