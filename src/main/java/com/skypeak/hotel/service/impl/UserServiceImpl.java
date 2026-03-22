package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Реализация сервиса {@link UserService} для управления пользователями.
 * <p>
 * Предоставляет бизнес-логику для поиска, изменения ролей и статусов пользователей.
 * Все операции, изменяющие данные, выполняются в транзакциях.
 *
 * @author Дмитрий Ельцов
 * @see UserRepository
 * @see RoleRepository
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(Pageable pageable) {
        log.info("▶️ Запрос на получение всех пользователей: страница={}, размер={}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserById(UUID id) {
        log.info("▶️ Запрос на получение пользователя по ID: {}", id);
        return getUserOrThrow(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeUserRole(UUID id, Role role) {
        log.info("▶️ Запрос на изменение роли для пользователя {}. Новая роль: {}", id, role);
        UserEntity user = getUserOrThrow(id);

        Role currentRole = user.getRole().getName();
        log.info("Текущая роль пользователя {}: {}", id, currentRole);

        if (currentRole == Role.ADMIN) {
            log.error("🚫 Попытка изменить роль администратора {}", id);
            throw new IllegalArgumentException("Роль администратора не может быть изменена.");
        }

        if (currentRole == role) {
            log.warn("⚠️ Пользователь {} уже имеет роль {}", id, role);
            throw new IllegalArgumentException("Пользователь уже имеет роль: " + role.name());
        }

        RoleEntity roleEntity = roleRepository.findByName(role).orElseThrow(() -> {
            log.error("🚫 Системная роль '{}' не найдена в базе данных!", role);
            return new EntityNotFoundException("Роль не найдена: " + role.name());
        });

        user.setRole(roleEntity);
        log.info("✅ Роль для пользователя {} успешно изменена с {} на {}", id, currentRole, role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateUser(UUID id) {
        log.info("▶️ Запрос на деактивацию пользователя: {}", id);
        UserEntity user = getUserOrThrow(id);

        if (user.getRole().getName().equals(Role.ADMIN)) {
            log.error("🚫 Попытка деактивировать администратора {}", id);
            throw new IllegalArgumentException("Статус администратора не может быть изменен.");
        }

        if (user.getStatus() == Status.INACTIVE) {
            log.warn("⚠️ Пользователь {} уже деактивирован", id);
            throw new IllegalArgumentException("Пользователь уже неактивен.");
        }

        user.setStatus(Status.INACTIVE);
        log.info("✅ Пользователь {} успешно деактивирован", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateUser(UUID id) {
        log.info("▶️ Запрос на активацию пользователя: {}", id);
        UserEntity user = getUserOrThrow(id);

        if (user.getStatus() == Status.ACTIVE) {
            log.warn("⚠️ Пользователь {} уже активен", id);
            throw new IllegalArgumentException("Пользователь уже активен.");
        }

        user.setStatus(Status.ACTIVE);
        log.info("✅ Пользователь {} успешно активирован", id);
    }

    /**
     * Вспомогательный метод для поиска пользователя по ID.
     * Выбрасывает исключение, если пользователь не найден.
     *
     * @param id UUID пользователя.
     * @return Найденный {@link UserEntity}.
     * @throws EntityNotFoundException если пользователь с указанным ID не существует.
     */
    private UserEntity getUserOrThrow(UUID id) {
        log.info("  🔎 Поиск пользователя по ID: {}", id);
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("🚫 Пользователь с ID {} не найден", id);
            return new EntityNotFoundException("Пользователь с ID " + id + " не найден.");
        });
    }
}
