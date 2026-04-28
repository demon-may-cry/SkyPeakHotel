package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserBalanceEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.util.TestFixtures;
import com.skypeak.hotel.util.UserTestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Тесты репозитория UserBalanceRepository")
class UserBalanceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Test
    @DisplayName("findByUser_Id - находит баланс по ID пользователя")
    void findByUser_Id_returnsBalance_whenExists() {
        // Given
        UserEntity user = new UserTestBuilder(entityManager)
                .email("user@skypeak.com")
                .password("password")
                .role(TestFixtures.getOrCreateRole(entityManager, Role.USER))
                .status(Status.ACTIVE)
                .build();

        entityManager.persistAndFlush(user);

        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(new BigDecimal("100.50"));
        balance.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(balance);

        entityManager.flush();

        // When
        Optional<UserBalanceEntity> userBalance = userBalanceRepository.findByUser_Id(user.getId());

        // Then
        assertThat(userBalance).isPresent();
        UserBalanceEntity foundBalance = userBalance.get();
        assertThat(foundBalance.getUser().getId()).isEqualTo(user.getId());
        assertThat(foundBalance.getBalance()).isEqualByComparingTo("100.50");
    }

    @Test
    @DisplayName("findByUser_Id - возвращает empty, если баланс не найден")
    void findByUser_Id_returnsEmpty_whenDoesNotExist() {
        // When
        Optional<UserBalanceEntity> userBalance = userBalanceRepository.findByUser_Id(UUID.randomUUID());

        // Then
        assertThat(userBalance).isEmpty();
    }
}
