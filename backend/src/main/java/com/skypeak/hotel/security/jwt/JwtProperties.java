package com.skypeak.hotel.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Конфигурационные свойства для JWT токенов.
 * <p>
 * Загружает параметры JWT из файла конфигурации (application.properties или application.yml)
 * с префиксом "jwt". Используется сервисом {@link JwtService} для создания и валидации токенов.
 * </p>
 *
 * <h3>Конфигурационные параметры:</h3>
 * <ul>
 *   <li><strong>jwt.secret</strong> - секретный ключ для подписи токенов (минимум 256 бит)</li>
 *   <li><strong>jwt.expiration-ms</strong> - время жизни токена в миллисекундах</li>
 * </ul>
 *
 * <h3>Пример конфигурации (application.properties):</h3>
 * <pre>
 * jwt.secret=mySecretKeyThatIsLongEnoughFor256BitHmacSha256Algorithm
 * jwt.expiration-ms=86400000
 * </pre>
 *
 * @author Дмитрий Ельцов
 * @see JwtService
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {

    /**
     * Секретный ключ для подписи JWT токенов.
     * <p>
     * Загружается из конфигурации с параметром jwt.secret.
     * Должен быть достаточно длинным для используемого алгоритма (минимум 256 бит для HS256).
     * </p>
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Время жизни JWT токена в миллисекундах.
     * <p>
     * Загружается из конфигурации с параметром jwt.expiration-ms.
     * По умолчанию установлено в 86400000 мс (24 часа).
     * </p>
     */
    @Value("${jwt.expiration-ms}")
    private long expirationMs;
}
