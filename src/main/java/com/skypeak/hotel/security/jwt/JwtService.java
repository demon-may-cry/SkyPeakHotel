package com.skypeak.hotel.security.jwt;

import com.skypeak.hotel.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Сервис для работы с JWT токенами.
 * <p>
 * Предоставляет методы для создания (generate), валидации (validate) и извлечения информации
 * из JWT токенов. Использует HMAC SHA-256 алгоритм для подписи токенов.
 * </p>
 *
 * <h3>Процесс работы:</h3>
 * <ul>
 *   <li>При логине: генерируется токен с email и ролью пользователя</li>
 *   <li>При запросе: токен извлекается из заголовка Authorization</li>
 *   <li>Токен валидируется и из него извлекаются email и роль</li>
 *   <li>На основе информации создается объект {@link CustomUserDetails}</li>
 * </ul>
 *
 * <h3>Структура JWT токена:</h3>
 * <ul>
 *   <li><strong>Header:</strong> алгоритм (HS256) и тип (JWT)</li>
 *   <li><strong>Payload:</strong> subject (email), claim "role", issued at, expiration</li>
 *   <li><strong>Signature:</strong> подпись на основе секретного ключа</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see JwtProperties
 * @see JwtAuthenticationFilter
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Генерирует JWT токен для пользователя.
     * <p>
     * Создает подписанный токен с информацией о пользователе (email и роль).
     * Токен подписывается секретным ключом и имеет определённый срок действия.
     * </p>
     *
     * @param email email пользователя (используется как subject токена)
     * @param role  роль пользователя (сохраняется как claim "role")
     * @return {@code String} сгенерированный JWT токен в виде строки
     */
    public String generateToken(String email, String role) {
        log.info("▶️ Генерация JWT токена для пользователя: {}", email);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        String token = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        log.info("✅ JWT токен успешно сгенерирован для: {}. Истечение: {} мс", email, jwtProperties.getExpirationMs());
        return token;
    }

    /**
     * Извлекает email пользователя из JWT токена.
     * <p>
     * Парсит и валидирует токен, затем возвращает subject (email) из payload.
     * </p>
     *
     * @param token JWT токен
     * @return {@code String} email пользователя из токена
     */
    public String extractEmail(String token) {
        log.debug("▶️ Извлечение email из JWT токена");
        String email = extractAllClaims(token).getSubject();
        log.debug("✅ Email успешно извлечен: {}", email);
        return email;
    }

    /**
     * Извлекает роль пользователя из JWT токена.
     * <p>
     * Парсит и валидирует токен, затем возвращает значение claim "role" из payload.
     * </p>
     *
     * @param token JWT токен
     * @return {@code String} роль пользователя из токена
     */
    @SuppressWarnings("unused")
    public String extractRole(String token) {
        log.debug("▶️ Извлечение роли из JWT токена");
        String role = extractAllClaims(token).get("role", String.class);
        log.debug("✅ Роль успешно извлечена: {}", role);
        return role;
    }

    /**
     * Проверяет валидность JWT токена.
     * <p>
     * Проверяет сигнатуру токена и срок его действия.
     * Токен считается невалидным, если подпись неверна или срок истек.
     * </p>
     *
     * @param token JWT токен для проверки
     * @return {@code true} если токен валиден, иначе {@code false}
     */
    public boolean isTokenValid(String token) {
        try {
            log.debug("▶️ Валидация JWT токена");
            extractAllClaims(token);
            log.debug("✅ JWT токен валиден");
            return true;
        } catch (Exception ex) {
            log.warn("⚠️ JWT токен невалиден. Причина: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Получает ключ для подписи токенов.
     * <p>
     * Преобразует секретный ключ из конфигурации в формат, требуемый для HMAC SHA-256.
     * </p>
     *
     * @return {@code SecretKey} для подписи токенов
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Извлекает все claims (утверждения) из JWT токена.
     * <p>
     * Парсит токен с проверкой сигнатуры и возвращает объект Claims,
     * содержащий все данные payload токена.
     * </p>
     *
     * @param token JWT токен
     * @return {@code Claims} объект с данными токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
