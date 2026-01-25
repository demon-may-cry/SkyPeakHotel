package com.skypeak.hotel.config;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.security.SecurityConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Дмитрий Ельцов
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${role.admin}")
    private String roleAdmin;

    @Value("${role.manager}")
    private String roleManager;

    @Value("${role.user}")
    private String roleUser;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Value("${admin.status}")
    private String status;

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        addRole();
        createAdmin();
    }

    private void createAdmin() {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("Admin with email {} already exists", email);
            return;
        }

        RoleEntity adminRoleEntity = roleRepository.findByName(roleAdmin)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        UserEntity admin = new UserEntity();

        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setStatus(status);
        admin.setRoleEntity(adminRoleEntity);

        userRepository.save(admin);
        log.info("Admin with email {} created successfully", email);
    }

    private void addRole() {
        List<String> roleList = Stream.of(roleAdmin, roleManager, roleUser).toList();
        for (String roleName : roleList) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setName(roleName);
                roleRepository.save(roleEntity);
                log.info("Role {} created", roleName);
            } else {
                log.info("Role {} already exists", roleName);
            }
        }
    }
}
