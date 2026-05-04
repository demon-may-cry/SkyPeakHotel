package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.entity.UserBalanceEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.repository.BalanceTransactionRepository;
import com.skypeak.hotel.repository.UserBalanceRepository;
import com.skypeak.hotel.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса BalanceServiceImpl")
class BalanceServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBalanceRepository balanceRepository;

    @Mock
    private BalanceTransactionRepository transactionRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;
    //TODO: error test
    @Test
    @DisplayName("getBalance - возвращает баланс, если он существует")
    void getBalance_returnsBalance_whenExists() {
        // Given
        UUID userId = UUID.randomUUID();
        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setBalance(new BigDecimal("150.00"));
        given(balanceRepository.findByUser_Id(userId)).willReturn(Optional.of(balance));

        // When
        BigDecimal result = balanceService.getBalance(userId);

        // Then
        assertThat(result).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("getBalance - возвращает 0, если баланс не существует")
    void getBalance_returnsZero_whenNotExists() {
        // Given
        UUID userId = UUID.randomUUID();
        given(balanceRepository.findByUser_Id(userId)).willReturn(Optional.empty());

        // When
        BigDecimal result = balanceService.getBalance(userId);

        // Then
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("deposit - успешно пополняет существующий баланс")
    void deposit_updatesExistingBalance() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);

        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(new BigDecimal("100.00"));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(balanceRepository.findByUser_Id(userId)).willReturn(Optional.of(balance));

        // When
        balanceService.deposit(userId, new BigDecimal("50.00"), "Test Deposit");

        // Then
        verify(balanceRepository).save(any(UserBalanceEntity.class));
        verify(transactionRepository).save(any(BalanceTransactionEntity.class));
        assertThat(balance.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("deposit - создает новый баланс, если его не было")
    void deposit_createsNewBalance_ifNotExists() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);

        UserBalanceEntity[] savedBalance = new UserBalanceEntity[1];

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(balanceRepository.findByUser_Id(userId)).willReturn(Optional.empty());
        given(balanceRepository.save(any(UserBalanceEntity.class))).willAnswer(invocation -> {
            savedBalance[0] = invocation.getArgument(0);
            return savedBalance[0];
        });

        // When
        balanceService.deposit(userId, new BigDecimal("50.00"), "Initial Deposit");

        // Then
        verify(balanceRepository, times(2)).save(any(UserBalanceEntity.class));
        verify(transactionRepository).save(any(BalanceTransactionEntity.class));
        assertThat(savedBalance[0]).isNotNull();
        assertThat(savedBalance[0].getUser()).isSameAs(user);
        assertThat(savedBalance[0].getBalance()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("deposit - выбрасывает исключение при отрицательной сумме")
    void deposit_throwsException_forNegativeAmount() {
        // Given
        UUID userId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> balanceService.deposit(userId, new BigDecimal("-50.00"), "Invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Сумма должна быть положительным числом.");
    }

    @Test
    @DisplayName("withdraw - успешно списывает средства")
    void withdraw_subtractsFromBalance_whenSufficientFunds() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);

        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(new BigDecimal("100.00"));

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(balanceRepository.findByUser_Id(userId)).willReturn(Optional.of(balance));

        // When
        balanceService.withdraw(userId, new BigDecimal("30.00"), "Test Withdraw");

        // Then
        verify(balanceRepository).save(any(UserBalanceEntity.class));
        verify(transactionRepository).save(any(BalanceTransactionEntity.class));
        assertThat(balance.getBalance()).isEqualByComparingTo("70.00");
    }

    @Test
    @DisplayName("withdraw - выбрасывает исключение при недостаточном балансе")
    void withdraw_throwsException_whenInsufficientFunds() {
        // Given
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);

        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setBalance(new BigDecimal("20.00"));

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(balanceRepository.findByUser_Id(userId)).willReturn(Optional.of(balance));

        // When & Then
        assertThatThrownBy(() -> balanceService.withdraw(userId, new BigDecimal("50.00"), "Too much"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Недостаточно средств на счете.");
    }

    @Test
    @DisplayName("getTransactions - возвращает страницу транзакций")
    void getTransactions_returnsPageOfTransactions() {
        // Given
        UUID userId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<BalanceTransactionEntity> page = new PageImpl<>(Collections.singletonList(new BalanceTransactionEntity()));
        given(transactionRepository.findByUser_Id(userId, pageable)).willReturn(page);

        // When
        Page<BalanceTransactionEntity> result = balanceService.getTransactions(userId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
