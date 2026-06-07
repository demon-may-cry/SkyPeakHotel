package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.service.UserService;
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
 *   <li>Email пользователей индексируется как уникальное поле</li>
 *   <li>Пароли хранятся в закодированном виде (BCrypt)</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 * @see UserService
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Находит пользователя по email с подгрузкой роли.
     * <p>
     * Используется при аутентификации пользователя для получения его данных и роли.
     * </p>
     *
     * @param email уникальный email пользователя
     * @return {@code Optional} с пользователем или пустой {@code Optional}
     */
    @EntityGraph(attributePaths = "role")
    Optional<UserEntity> findByEmail(String email);

    /**
     * Проверяет существование пользователя по email.
     * <p>
     * Используется для валидации при регистрации новых пользователей
     * и предотвращения дублирования учетных записей.
     * </p>
     *
     * @param email email для проверки
     * @return {@code true} если пользователь существует
     */
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Возвращает всех пользователей с пагинацией, подгружая роли.
     * <p>
     * Используется в административном интерфейсе для просмотра списка пользователей.
     * </p>
     *
     * @param pageable параметры пагинации (page, size, sort)
     * @return {@code Page} страницу пользователей с ролями
     */
    @EntityGraph(attributePaths = "role")
    @NonNull
    Page<UserEntity> findAll(@NonNull Pageable pageable);

    /**
     * Находит пользователя по ID с подгрузкой роли.
     * <p>
     * Используется для получения полных данных пользователя с его ролью.
     * </p>
     *
     * @param id уникальный идентификатор пользователя
     * @return {@code Optional} с пользователем или пустой {@code Optional}
     */
    @EntityGraph(attributePaths = "role")
    @NonNull
    Optional<UserEntity> findById(UUID id);
}