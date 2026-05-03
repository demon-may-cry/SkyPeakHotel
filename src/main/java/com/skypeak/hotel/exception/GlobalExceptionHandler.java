package com.skypeak.hotel.exception;

import com.skypeak.hotel.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST API.
 * <p>
 * Перехватывает все исключения, выброшенные контроллерами, и преобразует их в унифицированный
 * формат ошибки {@link ErrorResponse} с соответствующим HTTP статусом кодом.
 * </p>
 *
 * <h3>Обрабатываемые исключения:</h3>
 * <ul>
 *   <li><strong>EntityNotFoundException</strong> (404 NOT_FOUND) - объект не найден в БД</li>
 *   <li><strong>IllegalArgumentException</strong> (400 BAD_REQUEST) - некорректный аргумент</li>
 *   <li><strong>IllegalStateException</strong> (409 CONFLICT) - конфликт состояния</li>
 *   <li><strong>BadCredentialsException</strong> (401 UNAUTHORIZED) - неверные учетные данные</li>
 *   <li><strong>SecurityException</strong> (403 FORBIDDEN) - нарушение безопасности</li>
 *   <li><strong>AuthorizationDeniedException</strong> (403 FORBIDDEN) - доступ запрещен</li>
 *   <li><strong>MethodArgumentNotValidException</strong> (400 BAD_REQUEST) - ошибка валидации</li>
 *   <li><strong>HttpMessageNotReadableException</strong> (400 BAD_REQUEST) - неверный формат</li>
 *   <li><strong>Exception</strong> (500 INTERNAL_SERVER_ERROR) - неожиданное исключение</li>
 * </ul>
 *
 * <h3>Интеграция логирования:</h3>
 * <ul>
 *   <li>Все исключения логируются перед отправкой ответа клиенту</li>
 *   <li>Используется конфигурация Logback для правильного уровня логирования</li>
 *   <li>Каждый ответ включает временную метку, статус и описание ошибки</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see ErrorResponse
 * @see com.skypeak.hotel.dto.error.ErrorResponse
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Сообщение об ошибке: объект не найден.
     */
    private static final String ERROR_NOT_FOUND = "Объект не найден";

    /**
     * Сообщение об ошибке: ошибочный запрос.
     */
    private static final String ERROR_BAD_REQUEST = "Ошибочный запрос";

    /**
     * Сообщение об ошибке: конфликт состояния.
     */
    private static final String ERROR_CONFLICT = "Конфликт";

    /**
     * Сообщение об ошибке: доступ запрещен.
     */
    private static final String ERROR_FORBIDDEN = "Запрещено";

    /**
     * Сообщение об ошибке: внутренняя ошибка сервера.
     */
    private static final String INTERNAL_SERVER_ERROR = "Внутренняя ошибка сервера";

    /**
     * Сообщение об ошибке: неверный логин или пароль.
     */
    private static final String ERROR_UNAUTHORIZED = "Неверный логин или пароль";

    /**
     * Обрабатывает исключение EntityNotFoundException (ресурс не найден).
     * <p>
     * Возвращает HTTP статус 404 NOT_FOUND с описанием ошибки.
     * </p>
     *
     * @param ex      выброшенное исключение EntityNotFoundException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Ресурс не найден (404). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.NOT_FOUND,
                ERROR_NOT_FOUND,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение IllegalArgumentException (некорректный аргумент).
     * <p>
     * Возвращает HTTP статус 400 BAD_REQUEST с описанием ошибки валидации.
     * </p>
     *
     * @param ex      выброшенное исключение IllegalArgumentException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Некорректный запрос (400). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                ERROR_BAD_REQUEST,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение IllegalStateException (конфликт состояния).
     * <p>
     * Возвращает HTTP статус 409 CONFLICT когда операция невозможна из-за текущего состояния.
     * Пример: попытка отмены уже отменённого бронирования.
     * </p>
     *
     * @param ex      выброшенное исключение IllegalStateException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 409
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            IllegalStateException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Конфликт состояния (409). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.CONFLICT,
                ERROR_CONFLICT,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение BadCredentialsException (неверные учетные данные).
     * <p>
     * Возвращает HTTP статус 401 UNAUTHORIZED при неудачной попытке авторизации.
     * Используется при неверных email или пароле.
     * </p>
     *
     * @param ex      выброшенное исключение BadCredentialsException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            BadCredentialsException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Неудачная попытка авторизации (401). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.UNAUTHORIZED,
                ERROR_UNAUTHORIZED,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение SecurityException (нарушение безопасности).
     * <p>
     * Возвращает HTTP статус 403 FORBIDDEN при попытке выполнить операцию без необходимых прав.
     * Пример: пользователь пытается отменить бронирование другого пользователя.
     * </p>
     *
     * @param ex      выброшенное исключение SecurityException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 403
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            SecurityException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Нарушение безопасности (403). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.FORBIDDEN,
                ERROR_FORBIDDEN,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение AuthorizationDeniedException (доступ запрещен).
     * <p>
     * Возвращает HTTP статус 403 FORBIDDEN при попытке доступа к защищенному ресурсу.
     * Используется Spring Security для контроля ролевого доступа.
     * </p>
     *
     * @param ex      выброшенное исключение AuthorizationDeniedException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 403
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Доступ запрещен для пользователя (403). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.FORBIDDEN,
                ERROR_FORBIDDEN,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение MethodArgumentNotValidException (ошибка валидации).
     * <p>
     * Возвращает HTTP статус 400 BAD_REQUEST с перечислением всех ошибок валидации.
     * Используется Spring Validation для проверки DTO объектов.
     * </p>
     *
     * @param ex      выброшенное исключение MethodArgumentNotValidException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().
                getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("⚠️ Ошибка валидации входных данных (400). Адрес: {}. Ошибки: {}", request.getRequestURI(), message);
        return buildError(
                HttpStatus.BAD_REQUEST,
                ERROR_BAD_REQUEST,
                request.getRequestURI()
        );
    }

    /**
     * Обрабатывает исключение HttpMessageNotReadableException (неверный формат).
     * <p>
     * Возвращает HTTP статус 400 BAD_REQUEST когда JSON или другой формат не может быть распарсен.
     * Пример: передача невалидного JSON или неверного типа данных.
     * </p>
     *
     * @param ex      выброшенное исключение HttpMessageNotReadableException
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormat(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        log.warn("⚠️ Невалидный формат данных в теле запроса (400). Адрес: {}. Деталь: {}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                ERROR_BAD_REQUEST,
                request.getRequestURI());
    }

    /**
     * Обрабатывает неожиданные исключения (catch-all обработчик).
     * <p>
     * Этот метод вызывается для всех исключений, не обработанных более специфичными обработчиками.
     * Возвращает HTTP статус 500 INTERNAL_SERVER_ERROR и логирует полный stacktrace.
     * </p>
     *
     * @param ex      выброшенное исключение
     * @param request HTTP запрос от клиента
     * @return {@link ResponseEntity} с {@link ErrorResponse} и статусом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
            Exception ex,
            HttpServletRequest request) {
        log.error("❌ Неожиданное исключение (500). Адрес: {}. Тип: {}. Деталь: {}",
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
    }

    /**
     * Строит унифицированный ответ об ошибке.
     * <p>
     * Создает объект {@link ErrorResponse} с необходимой информацией об ошибке,
     * включая текущую дату/время, статус код, фразу статуса, сообщение об ошибке и путь запроса.
     * </p>
     *
     * @param status  HTTP статус кода
     * @param message кастомное сообщение об ошибке
     * @param path    путь к ресурсу, который вызвал ошибку
     * @return {@link ResponseEntity} с объектом {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            String message,
            String path
    ) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(status).body(error);
    }
}
