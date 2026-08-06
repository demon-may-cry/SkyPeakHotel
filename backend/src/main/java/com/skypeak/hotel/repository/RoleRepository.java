package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.security.CustomUserDetailsService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для доступа к данным о ролях пользователей ({@link RoleEntity}).
 * <p>
 * Предоставляет методы для поиска ролей по имени. Роли используются для контроля доступа
 * к различным функциям системы через Spring Security.
 * </p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Роли предопределены и неизменяемы (USER, MANAGER, ADMIN)</li>
 *   <li>Используются в GrantedAuthority для Spring Security</li>
 *   <li>Кэшируются для оптимизации производительности</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RoleEntity
 * @see Role
 * @see CustomUserDetailsService
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    /**
     * Находит роль по её имени (enum значению).
     * <p>
     * Используется для получения роли пользователя
     * при аутентификации и проверке прав доступа.
     * </p>
     *
     * @param name имя роли из enum {@link Role}
     * @return {@link Optional} с найденной ролью или пустой Optional
     */
    Optional<RoleEntity> findByName(@Size(max = 30) @NotNull Role name);
}