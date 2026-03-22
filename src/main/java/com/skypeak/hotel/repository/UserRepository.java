package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностями {@link UserEntity}.
 *
 * <p>Предоставляет методы для поиска, пагинации и проверки существования пользователей.</p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Все запросы с {@code @EntityGraph} загружают связанную роль пользователя (role) одним запросом (N+1 решение)</li>
 *   <li>Поддержка пагинации с аннотацией @NonNull для строгой типизации</li>
 *   <li>Оптимизированные методы для поиска по email и ID</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Находит пользователя по email с подгрузкой роли.
     *
     * @param email уникальный email пользователя
     * @return Optional с пользователем или пустой Optional
     */
    @EntityGraph(attributePaths = "role")
    Optional<UserEntity> findByEmail(String email);

    /**
     * Проверяет существование пользователя по email.
     *
     * @param email email для проверки
     * @return true если пользователь существует
     */
    boolean existsByEmail(String email);

    /**
     * Возвращает всех пользователей с пагинацией, подгружая роли.
     *
     * @param pageable параметры пагинации (page, size, sort)
     * @return страница пользователей с ролями
     */
    @EntityGraph(attributePaths = "role")
    @NonNull
    Page<UserEntity> findAll(@NonNull Pageable pageable);

    /**
     * Находит пользователя по ID с подгрузкой роли.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional с пользователем или пустой Optional
     */
    @EntityGraph(attributePaths = "role")
    @NonNull
    Optional<UserEntity> findById(UUID id);
}