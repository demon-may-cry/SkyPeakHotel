package com.skypeak.hotel.dto.balance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
public class DepositRequest {

    @NotNull
    @Positive
    private BigDecimal amount;
}
