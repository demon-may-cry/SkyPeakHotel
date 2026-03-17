package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.util.TestFixtures;
import com.skypeak.hotel.util.UserTestBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private void createUser(String email, String password, Role role, Status status) {

        UserEntity user = new UserTestBuilder(entityManager)
                .email(email)
                .password(password)
                .role(TestFixtures.getOrCreateRole(entityManager, role))
                .status(status)
                .build();

        entityManager.persistAndFlush(user);
    }

    @Test
    @DisplayName("findByEmail returns user when exists")
    void findByEmail_ReturnsUser_WhenExists() {
        // Given
        createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        // When
        Optional<UserEntity> result = userRepository.findByEmail("user@skypeak.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user@skypeak.com");
        assertThat(result.get().getRole()).isNotNull();
        assertThat(result.get().getRole().getName()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("existsByEmail returns true when user exists")
    void existsByEmail_ReturnsTrue_WhenExists() {
        // Given
        createUser("user@skypeak.com", "password", Role.USER, Status.ACTIVE);

        // When
        boolean exists = userRepository.existsByEmail("user@skypeak.com");
        boolean notExists = userRepository.existsByEmail("notuser@skypeak.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("findAll returns users with roles")
    void findAll_ReturnsUsersWithRoles() {
        // Given
        createUser("user1@skypeak.com", "password1", Role.USER, Status.ACTIVE);
        createUser("admin@skypeak.com", "password2", Role.ADMIN, Status.ACTIVE);
        createUser("manager@skypeak.com", "password3", Role.MANAGER, Status.ACTIVE);
        createUser("userInactive@skypeak.com", "password4", Role.USER, Status.INACTIVE);
        createUser("userBlocked@skypeak.com", "password5", Role.USER, Status.BLOCKED);

        // When
        var result = userRepository.findAll(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent().getFirst().getRole()).isNotNull();
    }

    @Test
    @DisplayName("findById returns user with role")
    void findById_ReturnsUserWithRole_WhenExists() {
        // Given
        UserEntity user = new UserTestBuilder(entityManager)
                .email("user@skypeak.com")
                .password("password")
                .role(TestFixtures.getOrCreateRole(entityManager, Role.USER))
                .status(Status.ACTIVE)
                .build();

        // When
        Optional<UserEntity> result = userRepository.findById(user.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getRole()).isNotNull();
        assertThat(result.get().getRole().getName()).isEqualTo(Role.USER);
    }
}
