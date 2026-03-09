package com.skypeak.hotel.exception;

import com.skypeak.hotel.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * @author Дмитрий Ельцов
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_NOT_FOUND = "NOT_FOUND";
    private static final String ERROR_BAD_REQUEST = "BAD_REQUEST";
    private static final String ERROR_CONFLICT = "CONFLICT";
    private static final String ERROR_FORBIDDEN = "FORBIDDEN";
    private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        log.error("Entity not found: {}", ex.getMessage());
        return buildError(
                HttpStatus.NOT_FOUND,
                ERROR_NOT_FOUND,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                ERROR_BAD_REQUEST,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            IllegalStateException ex,
            HttpServletRequest request) {
        log.error("Conflict: {}", ex.getMessage());
        return buildError(
                HttpStatus.CONFLICT,
                ERROR_CONFLICT,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            SecurityException ex,
            HttpServletRequest request) {
        log.error("Forbidden: {}", ex.getMessage());
        return buildError(
                HttpStatus.FORBIDDEN,
                ERROR_FORBIDDEN,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().
                getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.error("Validation error: {}", message);
        return buildError(
                HttpStatus.BAD_REQUEST,
                ERROR_BAD_REQUEST,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error: ", ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
    }

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
