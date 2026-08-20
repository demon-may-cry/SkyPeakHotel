package com.skypeak.hotel.security.jwt;

import com.skypeak.hotel.security.CustomUserDetails;
import com.skypeak.hotel.security.CustomUserDetailsService;
import com.skypeak.hotel.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для обработки JWT аутентификации.
 * <p>
 * Выполняется один раз за запрос (extends {@link OncePerRequestFilter}) и проверяет наличие
 * валидного JWT токена в заголовке Authorization. Если токен валиден, создается объект
 * аутентификации и устанавливается в SecurityContext.
 * </p>
 *
 * <h3>Процесс обработки запроса:</h3>
 * <ol>
 *   <li>Проверяется, находится ли запрос к Swagger/API docs (пропускаются)</li>
 *   <li>Извлекается заголовок Authorization</li>
 *   <li>Проверяется наличие и формат токена (должен начинаться с "Bearer ")</li>
 *   <li>Валидируется токен через {@link JwtService#isTokenValid(String)}</li>
 *   <li>Если валиден, извлекаются email и создается объект {@link CustomUserDetails}</li>
 *   <li>Создается {@link UsernamePasswordAuthenticationToken} и устанавливается в SecurityContext</li>
 *   <li>Запрос передается дальше по цепи фильтров</li>
 * </ol>
 *
 * <h3>Формат заголовка Authorization:</h3>
 * <pre>
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 * </pre>
 *
 * @author Дмитрий Ельцов
 * @see JwtService
 * @see CustomUserDetailsService
 * @see SecurityConfig
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Обрабатывает JWT аутентификацию для входящего запроса.
     * <p>
     * Проверяет наличие валидного JWT токена в заголовке Authorization,
     * и если найден, устанавливает аутентификацию в SecurityContext для доступа к защищенным ресурсам.
     * </p>
     *
     * @param request     HTTP запрос
     * @param response    HTTP ответ
     * @param filterChain цепь фильтров для передачи управления
     * @throws ServletException если возникла ошибка при обработке запроса
     * @throws IOException      если возникла ошибка ввода/вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Пропускаем обработку для swagger и api-docs
        if (request.getRequestURI().contains("/swagger") ||
                request.getRequestURI().contains("/v3/api-docs") ||
                request.getRequestURI().contains("/api/v1/analytics/visit")) {
            log.debug("🔓 Запрос к Swagger/API-docs {}. Пропускаем фильтр.", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // Проверяем наличие заголовка Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("🔓 Заголовок Authorization не найден или имеет неверный формат. Путь: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // Проверяем валидность токена
        if (!jwtService.isTokenValid(token)) {
            log.warn("⚠️ JWT токен невалиден. Путь: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.info("▶️ Обработка JWT токена для запроса: {}", request.getRequestURI());

        // Извлекаем email из токена и загружаем пользователя
        String email = jwtService.extractEmail(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Создаем объект аутентификации
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // Устанавливаем аутентификацию в SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("✅ Пользователь успешно аутентифицирован. Email: {}, Role: {}",
                email, userDetails.getAuthorities());

        filterChain.doFilter(request, response);
    }
}
