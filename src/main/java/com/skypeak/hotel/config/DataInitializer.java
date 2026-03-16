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

/**
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Slf4j
@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(String @NonNull ... args) {
        addRole();
        createAdmin();
    }

    private void createAdmin() {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("Administrator with email {} already exists", email);
            return;
        }

        var role = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new IllegalStateException(Role.ADMIN.name() + " role not found"));

        UserEntity admin = new UserEntity();

        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setStatus(Status.ACTIVE);
        admin.setRole(role);

        userRepository.save(admin);
        log.info("Administrator with email {} created successfully", email);
    }

    private void addRole() {
        for (Role role : Role.values()) {
            roleRepository.findByName(role)
                    .orElseGet(() -> {
                        var entity = new RoleEntity();
                        entity.setName(role);
                        log.info("Adding role: {}", role.name());
                        return roleRepository.save(entity);
            });
        }
    }
}
