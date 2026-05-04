package com.skypeak.hotel.security;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Status;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Пользовательское представление деталей пользователя для Spring Security.
 * <p>
 * Реализует интерфейс {@link UserDetails} и содержит информацию о пользователе,
 * загруженную из сущности {@link UserEntity}. Используется для аутентификации
 * и авторизации запросов.
 * </p>
 *
 * <h3>Информация о пользователе:</h3>
 * <ul>
 *   <li>{@link #id} - UUID пользователя</li>
 *   <li>{@link #email} - используется как username для Spring Security</li>
 *   <li>{@link #password} - закодированный пароль (BCrypt)</li>
 *   <li>{@link #status} - статус пользователя (ACTIVE/INACTIVE/BLOCKED)</li>
 *   <li>{@link #authorities} - роли пользователя (ROLE_USER, ROLE_MANAGER, ROLE_ADMIN)</li>
 * </ul>
 *
 * <h3>Статус пользователя:</h3>
 * <ul>
 *   <li>Аккаунт считается включенным только если статус = ACTIVE</li>
 *   <li>Неактивные или заблокированные пользователи не могут выполнять операции</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 * @see UserDetails
 * @see Authentication
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final Status status;
    private final List<GrantedAuthority> authorities;

    /**
     * Конструктор для создания CustomUserDetails из сущности пользователя.
     * <p>
     * Извлекает из сущности все необходимые данные и формирует список прав доступа
     * (authorities) на основе роли пользователя.
     * </p>
     *
     * @param user сущность пользователя из базы данных
     */
    public CustomUserDetails(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
    }

    /**
     * Возвращает список полномочий (GrantedAuthority) пользователя.
     * <p>
     * Список содержит одну роль, префиксированную "ROLE_" (например, ROLE_USER, ROLE_ADMIN).
     * Используется Spring Security для проверки доступа.
     * </p>
     *
     * @return {@code Collection} коллекция полномочий пользователя
     */
    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль пользователя.
     * <p>
     * Пароль хранится в закодированном виде и используется для проверки
     * при аутентификации.
     * </p>
     *
     * @return {@code String} закодированный пароль пользователя
     */
    @Override
    public @Nullable String getPassword() {
        return password;
    }

    /**
     * Возвращает username пользователя.
     * <p>
     * В этой реализации username - это email пользователя, используется
     * для уникальной идентификации при аутентификации.
     * </p>
     *
     * @return {@code String} email пользователя (используется как username)
     */
    @Override
    @NullMarked
    public String getUsername() {
        return email;
    }

    /**
     * Проверяет, не истек ли срок действия аккаунта.
     * <p>
     * В этой реализации всегда возвращает true, так как система не использует
     * механизм истечения срока аккаунта.
     * </p>
     *
     * @return {@code true} - аккаунт не истек
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверяет, не заблокирован ли аккаунт на уровне Spring Security.
     * <p>
     * В этой реализации всегда возвращает true, блокировка контролируется
     * через статус пользователя (см. {@link #isEnabled()}).
     * </p>
     *
     * @return {@code true} - аккаунт не заблокирован
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверяет, не истекла ли учетная информация пользователя.
     * <p>
     * В этой реализации всегда возвращает true, так как система не использует
     * механизм истечения учетной информации.
     * </p>
     *
     * @return {@code true} - учетная информация не истекла
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверяет, включен ли аккаунт пользователя.
     * <p>
     * Аккаунт считается включенным только если статус пользователя = ACTIVE.
     * Если пользователь в статусе INACTIVE или BLOCKED, аккаунт отключен.
     * </p>
     *
     * @return {@code true} если статус = ACTIVE, иначе {@code false}
     */
    @Override
    public boolean isEnabled() {
        return status == Status.ACTIVE;
    }
}
