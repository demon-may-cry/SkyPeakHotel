package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.auth.RegisterRequest;
import com.skypeak.hotel.entity.UserBalanceEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserBalanceRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.RegistrationService;
import lombok.RequiredArgsConstructor;
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
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserBalanceRepository balanceRepository;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalStateException("User with this email already exists");

        var role = roleRepository.findByName("USER").orElseThrow(() ->
                new IllegalStateException("Role USER not found"));

        var user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleEntity(role);
        user.setStatus("ACTIVE");

        userRepository.save(user);

        var balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(BigDecimal.ZERO);
        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);
    }
}
