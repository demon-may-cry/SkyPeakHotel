package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.auth.RegisterRequest;
import com.skypeak.hotel.entity.UserBalanceEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserBalanceRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Реализация {@link RegistrationService} для регистрации новых пользователей.
 * <p>
 * Этот сервис выполняет следующие операции в рамках одной транзакции:
 * <ol>
 *     <li>Проверяет, не занят ли указанный email.</li>
 *     <li>Находит роль "USER" в системе.</li>
 *     <li>Создает и сохраняет новую сущность {@link UserEntity} с зашифрованным паролем.</li>
 *     <li>Создает и сохраняет начальный нулевой баланс {@link UserBalanceEntity} для нового пользователя.</li>
 * </ol>
 * В случае ошибки (например, если email уже существует или роль "USER" не найдена),
 * транзакция откатывается, и в базе данных не остается никаких изменений.
 *
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserBalanceRepository balanceRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(RegisterRequest request) {
        log.info("▶️ Начинаем регистрацию нового пользователя с email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("🚫 Пользователь с email {} уже существует", request.getEmail());
            throw new IllegalStateException("Пользователь с таким email уже существует");
        }
        log.info("✅ Email {} свободен для регистрации", request.getEmail());

        var role = roleRepository.findByName(Role.USER).orElseThrow(() -> {
            log.error("🚫 Системная роль 'USER' не найдена в базе данных!");
            return new IllegalStateException("Роль 'USER' не найдена. Регистрация невозможна.");
        });
        log.info("✅ Роль 'USER' найдена");

        var user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        log.info("  ➕ Создаем сущность пользователя: email={}, роль={}, статус={}",
                user.getEmail(),
                user.getRole().getName(),
                user.getStatus());

        userRepository.save(user);
        log.info("✅ Пользователь {} успешно сохранен в базе данных", user.getEmail());

        var balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(BigDecimal.ZERO);
        balance.setUpdatedAt(LocalDateTime.now());
        log.info("  ➕ Создаем начальный баланс для пользователя {}", user.getEmail());

        balanceRepository.save(balance);
        log.info("✅ Начальный баланс для {} успешно создан. Сумма: {}",
                user.getEmail(),
                balance.getBalance());
        
        log.info("🎉 Пользователь {} полностью зарегистрирован!", user.getEmail());
    }
}
