package com.skypeak.hotel.config;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.text.MessageFormat.format;

/**
 * Инициализатор начальных данных для приложения.
 *
 * <p>Выполняется автоматически при старте Spring Boot приложения (реализует {@link CommandLineRunner}).
 * Создает все необходимые роли пользователей и администратора с учетными данными из конфигурации.</p>
 *
 * <p><strong>Особенности:</strong></p>
 * <ul>
 *   <li>Создает все enum-роли {@link Role} если они отсутствуют</li>
 *   <li>Создает администратора только если его нет по email</li>
 *   <li>Пароль администратора кодируется через {@link PasswordEncoder}</li>
 *   <li>Все операции транзакционны {@code @Transactional} </li>
 *   <li>Логирует все операции инициализации</li>
 * </ul>
 *
 * <p><strong>Конфигурация:</strong></p>
 * <pre>
 * admin.email=admin@skypeak.com
 * admin.password=admin123
 * </pre>
 *
 * @author Дмитрий Ельцов
 * @see CommandLineRunner
 * @see RoleRepository
 * @see UserRepository
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Email администратора из application.properties */
    @Value("${admin.email}")
    private String email;

    /** Пароль администратора из application.properties (будет закодирован) */
    @Value("${admin.password}")
    private String password;

    @Value("${admin.first.name}")
    private String firstName;

    @Value("${admin.last.name}")
    private String lastName;

    @Value("${admin.phone.number}")
    private String phoneNumber;

    /**
     * Основной метод инициализации, вызывается Spring Boot при старте.
     * Последовательно создает роли и администратора.
     */
    @Override
    public void run(String @NonNull ... args) {
        log.info("=== Начинаем инициализацию данных ===");
        addRoles();
        createAdmin();
        log.info("=== Инициализация данных завершена ===");
    }

    /**
     * Создает администратора если его нет в системе.
     * Проверяет существование по email, роль {@code ADMIN} должна существовать заранее.
     */
    private void createAdmin() {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("✅ Администратор {} уже существует, пропускаем создание", email);
            return;
        }

        var role = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        format("🚫 Роль {0} не найдена!", Role.ADMIN.name())));

        UserEntity admin = new UserEntity();

        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setStatus(Status.ACTIVE);
        admin.setRole(role);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setPhoneNumber(phoneNumber);

        userRepository.save(admin);
        log.info("✅ Администратор {} успешно создан", email);
    }

    /**
     * Идемпотентно создает все роли из enum {@link Role}.
     * Если роль существует - пропускает, иначе создает новую.
     */
    private void addRoles() {
        log.info("🔄 Проверяем наличие ролей...");
        for (Role role : Role.values()) {
            roleRepository.findByName(role)
                    .orElseGet(() -> {
                        var entity = new RoleEntity();
                        entity.setName(role);
                        log.info("  ➕ Создана роль: {}", role.name());
                        return roleRepository.save(entity);
            });
        }
        log.info("✅ Все роли проверены/созданы");
    }
}
