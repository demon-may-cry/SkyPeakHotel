package com.skypeak.hotel.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Сервис для получения информации о текущем аутентифицированном пользователе.
 * <p>
 * Инкапсулирует работу с {@link SecurityContextHolder}, предоставляя удобные
 * методы для получения данных текущего пользователя.
 * </p>
 *
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class CurrentUserService {

    /**
     * Возвращает объект текущего аутентифицированного пользователя.
     *
     * @return {@link CustomUserDetails}
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    public CustomUserDetails getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            log.warn("⚠️ Не удалось получить данные текущего пользователя.");
            throw new IllegalStateException("Пользователь не аутентифицирован.");
        }

        return user;
    }

    /**
     * Возвращает email текущего пользователя.
     *
     * @return email пользователя
     */
    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    /**
     * Возвращает UUID текущего пользователя.
     *
     * @return UUID пользователя
     */
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Возвращает имя текущего пользователя.
     *
     * @return имя пользователя
     */
    public String getCurrentUserFirstName() {
        return getCurrentUser().getFirstName();
    }

    /**
     * Возвращает роль текущего пользователя.
     *
     * @return роль пользователя
     */
    public String getCurrentUserRole() {
        return getCurrentUser()
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow()
                .getAuthority();
    }

    /**
     * Проверяет, аутентифицирован ли пользователь.
     *
     * @return true если пользователь вошел в систему
     */
    public boolean isAuthenticated() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Возвращает объект Authentication.
     *
     * @return Authentication
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    private Authentication getAuthentication() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {

            log.warn("⚠️ Попытка обращения без аутентификации.");

            throw new IllegalStateException(
                    "Пользователь не аутентифицирован."
            );
        }

        return authentication;
    }

}
