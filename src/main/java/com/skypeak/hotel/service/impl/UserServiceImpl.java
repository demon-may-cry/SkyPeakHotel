package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.user.UpdateUserProfileRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import com.skypeak.hotel.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.text.MessageFormat.format;

/**
 * Реализация сервиса {@link UserService} для управления пользователями.
 * <p>
 * Предоставляет бизнес-логику для поиска, изменения ролей и статусов пользователей.
 * Все операции, изменяющие данные, выполняются в транзакциях.
 *
 * @author Дмитрий Ельцов
 * @see UserService
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
        checkOwnerOrAdmin(id);
        return getUserOrThrow(id);
    }

    @Override
    public void updateUserProfile(UUID id, UpdateUserProfileRequest request) {
        log.info("▶️ Запрос на обновление профиля пользователя: {}", id);
        checkOwnerOrAdmin(id);
        UserEntity user = getUserOrThrow(id);

        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
            log.info("✅ Пароль пользователя {} успешно обновлен", id);
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
            log.info("✅ Имя пользователя {} успешно обновлено", id);
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
            log.info("✅ Фамилия пользователя {} успешно обновлена", id);
        }

        if (request.middleName() != null) {
            user.setMiddleName(request.middleName());
            log.info("✅ Отчество пользователя {} успешно обновлено", id);
        }

        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
            log.info("✅ Дата рождения пользователя {} успешно обновлена", id);
        }

        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
            log.info("✅ URL аватара пользователя {} успешно обновлен", id);
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("✅ Профиль пользователя {} успешно обновлен", id);

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

        if (currentRole.equals(Role.ADMIN)) {
            log.error("🚫 Попытка изменить роль администратора {}", id);
            throw new IllegalArgumentException("Роль администратора не может быть изменена.");
        }

        if (currentRole.equals(role)) {
            log.warn("⚠️ Пользователь {} уже имеет роль {}", id, role);
            throw new IllegalArgumentException(format("Пользователь уже имеет роль: {0}", role.name()));
        }

        RoleEntity roleEntity = roleRepository.findByName(role).orElseThrow(() -> {
            log.error("🚫 Системная роль '{}' не найдена в базе данных!", role);
            return new EntityNotFoundException(format("Роль не найдена: {0}", role.name()));
        });

        user.setRole(roleEntity);
        userRepository.save(user);
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

        if (user.getStatus().equals(Status.INACTIVE)) {
            log.warn("⚠️ Пользователь {} уже деактивирован", id);
            throw new IllegalArgumentException("Пользователь уже неактивен.");
        }

        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        log.info("✅ Пользователь {} успешно деактивирован", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateUser(UUID id) {
        log.info("▶️ Запрос на активацию пользователя: {}", id);
        UserEntity user = getUserOrThrow(id);

        if (user.getStatus().equals(Status.ACTIVE)) {
            log.warn("⚠️ Пользователь {} уже активен", id);
            throw new IllegalArgumentException("Пользователь уже активен.");
        }

        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
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
            return new EntityNotFoundException(format("Пользователь с ID {0} не найден.", id));
        });
    }

    /**
     * Быстрая проверка доступа: разрешаем, если текущий пользователь является владельцем ресурса
     * или имеет роль MANAGER / ADMIN. В противном случае выбрасываем AccessDeniedException.
     */
    private void checkOwnerOrAdmin(UUID resourceOwnerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Пользователь не авторизован");
        }

        boolean isManagerOrAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                        || "ROLE_MANAGER".equals(a.getAuthority()));

        if (isManagerOrAdmin) {
            return; // менеджеры и админы имеют доступ
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails user) {
            if (user.getId() != null && user.getId().equals(resourceOwnerId)) {
                return; // владелец ресурса
            }
        }

        throw new AccessDeniedException("Доступ запрещён");
    }
}
