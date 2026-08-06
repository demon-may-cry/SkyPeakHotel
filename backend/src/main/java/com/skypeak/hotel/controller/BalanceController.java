package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.balance.BalanceResponse;
import com.skypeak.hotel.dto.balance.DepositRequest;
import com.skypeak.hotel.dto.balance.TransactionResponse;
import com.skypeak.hotel.mapper.BalanceTransactionMapper;
import com.skypeak.hotel.security.CustomUserDetails;
import com.skypeak.hotel.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Контроллер для управления балансом пользователя.
 * <p>
 * Предоставляет эндпоинты для получения текущего баланса,
 * пополнения счета и просмотра истории транзакций.
 * Все операции выполняются в контексте аутентифицированного пользователя.
 *
 * @author Дмитрий Ельцов
 * @see BalanceService
 * @see CustomUserDetails
 */
@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
@Slf4j
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceTransactionMapper transactionMapper;

    /**
     * Возвращает текущий баланс аутентифицированного пользователя.
     *
     * @param userDetails данные аутентифицированного пользователя.
     * @return {@link ResponseEntity} с {@link BalanceResponse} и статусом 200 OK.
     */
    @GetMapping
    public ResponseEntity<BalanceResponse> getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("▶️ Запрос на получение баланса для пользователя: {}", userDetails.getUsername());
        BigDecimal balance = balanceService.getBalance(userDetails.getId());
        log.info("✅ Успешно возвращен баланс для пользователя {}: {}", userDetails.getUsername(), balance);
        return ResponseEntity.ok(new BalanceResponse(balance));
    }

    /**
     * Пополняет баланс аутентифицированного пользователя.
     *
     * @param request     DTO с суммой пополнения.
     * @param userDetails данные аутентифицированного пользователя.
     */
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@RequestBody @Valid DepositRequest request,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("▶️ Запрос на пополнение баланса для пользователя {} на сумму {}", userDetails.getUsername(), request.getAmount());
        balanceService.deposit(
                userDetails.getId(),
                request.getAmount(),
                "Пополнение баланса пользователя");
        log.info("✅ Баланс пользователя {} успешно пополнен на {}", userDetails.getUsername(), request.getAmount());
    }

    /**
     * Возвращает пагинированную историю транзакций для аутентифицированного пользователя.
     *
     * @param userDetails данные аутентифицированного пользователя.
     * @param pageable    параметры пагинации.
     * @return {@link Page} с {@link TransactionResponse}.
     */
    @GetMapping("/transactions")
    public Page<TransactionResponse> getTransactions(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PageableDefault(
                                                             sort = "createdAt",
                                                             direction = DESC
                                                     )
                                                     Pageable pageable) {
        log.info("▶️ Запрос на получение истории транзакций для пользователя: {}. Параметры: {}", userDetails.getUsername(), pageable);
        Page<TransactionResponse> transactions = balanceService
                .getTransactions(userDetails.getId(), pageable)
                .map(transactionMapper::toDto);
        log.info("✅ Успешно возвращено {} транзакций для пользователя {}", transactions.getNumberOfElements(), userDetails.getUsername());
        return transactions;
    }
}
