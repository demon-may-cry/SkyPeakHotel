package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.balance.DepositRequest;
import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.mapper.BalanceTransactionMapper;
import com.skypeak.hotel.security.CustomUserDetails;
import com.skypeak.hotel.security.CustomUserDetailsService;
import com.skypeak.hotel.security.jwt.JwtAuthenticationFilter;
import com.skypeak.hotel.security.jwt.JwtService;
import com.skypeak.hotel.service.BalanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//TODO: BalanceControllerTest
@WebMvcTest(BalanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Тесты контроллера BalanceController")
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BalanceService balanceService;

    @MockitoBean
    private BalanceTransactionMapper transactionMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity testUserEntity() {
        RoleEntity role = new RoleEntity();
        role.setName(Role.USER);

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail("user@skypeak.com");
        user.setPassword("password");
        user.setStatus(Status.ACTIVE);
        user.setRole(role);
        return user;
    }

    @Test
    @DisplayName("GET /balance - Успешное получение баланса")
    void getBalance_returnsBalance_whenAuthenticated() throws Exception {
        /*// Given
        UserEntity user = testUserEntity();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // Настраиваем мок UserDetailsService
        given(customUserDetailsService.loadUserByUsername(user.getEmail())).willReturn(userDetails);
        given(balanceService.getBalance(user.getId())).willReturn(new BigDecimal("123.45"));

        var result = customUserDetailsService.loadUserByUsername(user.getEmail());

        System.out.println(format("Result: {0} | {1}",
                result.getUsername(),
                result.getAuthorities()));

        System.out.println(format("Auth: {0} | {1}",
                auth.getName(),
                auth.getAuthorities()));

        // When & Then
        mockMvc.perform(get("/api/v1/balance")
                        .with(authentication(auth)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance").value(123.45));

        verify(balanceService).getBalance(user.getId());*/
    }

    @Test
    @DisplayName("POST /deposit - Успешное пополнение баланса")
    void deposit_succeeds_whenRequestIsValid() throws Exception {
        /*// Given
        UserEntity user = testUserEntity();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        //given(customUserDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(new BigDecimal("100.00"));

        // When & Then
        mockMvc.perform(post("/api/v1/balance/deposit")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());

        verify(balanceService).deposit(user.getId(), new BigDecimal("100.00"), "Пополнение баланса пользователя");*/
    }

    @Test
    @DisplayName("POST /deposit - Ошибка при невалидном запросе (сумма <= 0)")
    void deposit_fails_whenAmountIsInvalid() throws Exception {
        /*// Given
        UserEntity user = testUserEntity();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        //given(customUserDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);

        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAmount(new BigDecimal("-50.00"));

        // When & Then
        mockMvc.perform(post("/api/v1/balance/deposit")
                        .with(user(user.getEmail()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());*/
    }

    @Test
    @DisplayName("GET /transactions - Успешное получение транзакций")
    void getTransactions_returnsPageOfTransactions() throws Exception {
        /*// Given
        UserEntity user = testUserEntity();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        //given(customUserDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);

        Page<BalanceTransactionEntity> page = new PageImpl<>(Collections.emptyList());
        given(balanceService.getTransactions(any(UUID.class), any(Pageable.class))).willReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/balance/transactions")
                        .with(user(user.getEmail())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(balanceService).getTransactions(user.getId(), Pageable.unpaged());*/
    }
}
