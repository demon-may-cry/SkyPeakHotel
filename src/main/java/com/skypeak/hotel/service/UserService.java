package com.skypeak.hotel.service;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.service.impl.UserServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Сервис для управления пользователями.
 * <p>
 * Предоставляет контракт для выполнения операций над сущностями пользователей,
 * таких как поиск, изменение ролей и статуса.
 *
 * @author Дмитрий Ельцов
 * @see UserServiceImpl
 */
public interface UserService {

    /**
     * Возвращает пагинированный список всех пользователей.
     *
     * @param pageable объект с параметрами пагинации (номер страницы, размер, сортировка).
     * @return {@link Page} с пользователями.
     */
    Page<UserEntity> findAll(Pageable pageable);

    /**
     * Находит пользователя по его уникальному идентификатору (ID).
     *
     * @param id UUID пользователя.
     * @return {@link UserEntity} найденного пользователя.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь с указанным ID не найден.
     */
    UserEntity getUserById(UUID id);

    /**
     * Изменяет роль указанного пользователя.
     *
     * @param id      UUID пользователя, чья роль будет изменена.
     * @param role новая роль, которую необходимо присвоить.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь или целевая роль не найдены.
     * @throws IllegalArgumentException                      если пытаются изменить роль администратора или присвоить уже имеющуюся роль.
     */
    void changeUserRole(UUID id, Role role);

    /**
     * Деактивирует пользователя, устанавливая ему статус {@code INACTIVE}.
     *
     * @param id UUID пользователя для деактивации.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь не найден.
     * @throws IllegalArgumentException                      если пользователь уже неактивен или является администратором.
     */
    void deactivateUser(UUID id);

    /**
     * Активирует пользователя, устанавливая ему статус {@code ACTIVE}.
     *
     * @param id UUID пользователя для активации.
     * @throws jakarta.persistence.EntityNotFoundException если пользователь не найден.
     * @throws IllegalArgumentException                      если пользователь уже активен.
     */
    void activateUser(UUID id);
}
