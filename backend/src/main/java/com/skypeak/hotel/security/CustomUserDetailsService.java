package com.skypeak.hotel.security;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис для загрузки деталей пользователя для Spring Security.
 * <p>
 * Реализует интерфейс {@link UserDetailsService} и используется Spring Security
 * для загрузки информации о пользователе по его username (email) при аутентификации.
 * </p>
 *
 * <h3>Процесс аутентификации:</h3>
 * <ol>
 *   <li>Spring Security вызывает {@link #loadUserByUsername(String)} с email пользователя</li>
 *   <li>Сервис ищет пользователя в базе данных по email</li>
 *   <li>Если найден, возвращает {@link CustomUserDetails} с информацией о пользователе</li>
 *   <li>Spring Security использует этот объект для проверки пароля и создания токена</li>
 * </ol>
 *
 * @author Дмитрий Ельцов
 * @see CustomUserDetails
 * @see UserRepository
 * @see UserDetailsService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает детали пользователя по его email (username).
     * <p>
     * Используется Spring Security при процессе аутентификации пользователя.
     * Ищет пользователя в базе данных по email и возвращает {@link CustomUserDetails}
     * или выбрасывает исключение, если пользователь не найден.
     * </p>
     *
     * @param username email пользователя
     * @return {@link CustomUserDetails} с информацией о пользователе
     * @throws UsernameNotFoundException если пользователь с указанным email не найден
     */
    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("▶️ Поиск пользователя по email: {}", username);
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() -> {
            log.warn("⚠️ Пользователь не найден. Email: {}", username);
            return new UsernameNotFoundException("Пользователь с email " + username + " не найден");
        });
        log.info("✅ Пользователь успешно загружен. Email: {}, Role: {}", username, userEntity.getRole().getName());
        return new CustomUserDetails(userEntity);
    }
}
