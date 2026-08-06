package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.entity.enums.TransactionType;
import com.skypeak.hotel.util.TestFixtures;
import com.skypeak.hotel.util.UserTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Тесты репозитория BalanceTransactionRepository")
class BalanceTransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BalanceTransactionRepository transactionRepository;

    private UserEntity user;

    private void createTransaction(UserEntity user, String amount, TransactionType type, LocalDateTime createdAt) {
        BalanceTransactionEntity tx = new BalanceTransactionEntity();
        tx.setUser(user);
        tx.setAmount(new BigDecimal(amount));
        tx.setType(type);
        tx.setDescription("Test transaction");
        tx.setCreatedAt(createdAt);
        entityManager.persist(tx);
    }

    @BeforeEach
    void setUp() {
        user = new UserTestBuilder(entityManager)
                .email("user@skypeak.com")
                .password("password")
                .role(TestFixtures.getOrCreateRole(entityManager, Role.USER))
                .status(Status.ACTIVE)
                .build();

        entityManager.persist(user);

        createTransaction(user, "100.00", TransactionType.DEPOSIT, LocalDateTime.now().minusDays(2));
        createTransaction(user, "50.00", TransactionType.WITHDRAW, LocalDateTime.now().minusDays(1));
        createTransaction(user, "200.00", TransactionType.DEPOSIT, LocalDateTime.now());

        entityManager.flush();
    }

    @Test
    @DisplayName("findByUser_Id - находит все транзакции для пользователя")
    void findByUser_Id_returnsAllTransactionsForUser() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<BalanceTransactionEntity> result = transactionRepository.findByUser_Id(user.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent()).allMatch(tx -> tx.getUser().getId().equals(user.getId()));
    }

    @Test
    @DisplayName("findByUser_Id - правильно применяет пагинацию")
    void findByUser_Id_appliesPaginationCorrectly() {
        // Given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").ascending());

        // When
        Page<BalanceTransactionEntity> result = transactionRepository.findByUser_Id(user.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent().get(0).getAmount()).isEqualByComparingTo("100.00");
        assertThat(result.getContent().get(1).getAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("findByUser_Id - возвращает пустую страницу, если у пользователя нет транзакций")
    void findByUser_Id_returnsEmptyPage_whenNoTransactions() {
        // Given
        UserEntity admin = new UserTestBuilder(entityManager)
                .email("admin@skypeak.com")
                .password("password")
                .role(TestFixtures.getOrCreateRole(entityManager, Role.ADMIN))
                .status(Status.ACTIVE)
                .build();

        entityManager.persistAndFlush(admin);

        Pageable pageable = PageRequest.of(0, 5);

        // When
        Page<BalanceTransactionEntity> result = transactionRepository.findByUser_Id(admin.getId(), pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }
}
