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

    @Override
    public void register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalStateException("User with this email already exists");
        log.info("Email {} is available for registration", request.getEmail());

        var role = roleRepository.findByName(Role.USER).orElseThrow(() ->
                new IllegalStateException("Role USER not found"));
        log.warn("Role USER found for registration");

        var user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        log.info("Creating user entity: {}, {}, {}",
                user.getEmail(),
                user.getRole().getName(),
                user.getStatus());

        userRepository.save(user);
        log.info("User {} registered successfully", user.getEmail());

        var balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(BigDecimal.ZERO);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);
        log.info("Initial balance created for user {} with amount {}",
                user.getEmail(),
                balance.getBalance());
    }
}
