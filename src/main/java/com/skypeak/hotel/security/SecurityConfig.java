package com.skypeak.hotel.security;

import com.skypeak.hotel.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфигурация Spring Security для приложения.
 * <p>
 * Определяет правила авторизации, создает фильтр цепь для обработки JWT токенов,
 * и настраивает кодирование паролей с использованием BCrypt.
 * </p>
 *
 * <h3>Принцип работы:</h3>
 * <ul>
 *   <li>CSRF защита отключена (для REST API не требуется)</li>
 *   <li>Форма и Basic аутентификация отключены (используется JWT)</li>
 *   <li>JWT фильтр добавляется перед {@code UsernamePasswordAuthenticationFilter}</li>
 *   <li>Сессии отключены для использования stateless архитектуры</li>
 *   <li>Открытый доступ к публичным эндпоинтам (swagger, auth, health)</li>
 * </ul>
 *
 * <h3>Защищенные эндпоинты:</h3>
 * <ul>
 *   <li>Все эндпоинты требуют JWT токен в заголовке Authorization</li>
 *   <li>Эндпоинты пролеливаются через @PreAuthorize для контроля ролей</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see JwtAuthenticationFilter
 * @see EnableMethodSecurity
 */
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Создает и конфигурирует цепь фильтров безопасности.
     * <p>
     * Определяет какие эндпоинты доступны без аутентификации и какие требуют JWT токен.
     * Интегрирует JWT фильтр для обработки токенов при каждом запросе.
     * </p>
     *
     * @param http объект для конфигурации Spring Security
     * @return сконфигурированная {@link SecurityFilterChain}
     */
    @Bean
    @SuppressWarnings("unused")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/rooms",
                                "/api/v1/rooms/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/error",
                                "/actuator/health",
                                "/swagger/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))))
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Создает bean для кодирования паролей с использованием BCrypt.
     * <p>
     * BCrypt автоматически добавляет соль и использует адаптивное хеширование,
     * что делает его защищенным от перебора паролей.
     * </p>
     *
     * @return {@link PasswordEncoder} экземпляр BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает bean для менеджера аутентификации.
     * <p>
     * Используется для программной аутентификации пользователей
     * (например, при логине пользователя).
     * </p>
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return {@link AuthenticationManager} для обработки аутентификации
     */
    @Bean
    @SuppressWarnings("unused")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
