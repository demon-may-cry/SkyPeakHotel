package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.balance.BalanceResponse;
import com.skypeak.hotel.dto.balance.DepositRequest;
import com.skypeak.hotel.dto.balance.TransactionResponse;
import com.skypeak.hotel.mapper.BalanceTransactionMapper;
import com.skypeak.hotel.security.CustomUserDetails;
import com.skypeak.hotel.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceTransactionMapper transactionMapper;

    @GetMapping
    public BalanceResponse getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return new BalanceResponse(balanceService.getBalance(userDetails.getId()));
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody DepositRequest request,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

        balanceService.deposit(
                userDetails.getId(),
                request.getAmount(),
                "User balance deposit");
    }

    @GetMapping("/transactions")
    public Page<TransactionResponse> getTransactions(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     Pageable pageable) {
        return balanceService
                .getTransactions(userDetails.getId(), pageable)
                .map(transactionMapper::toDto);
    }

}

