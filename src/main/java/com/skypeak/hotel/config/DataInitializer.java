package com.skypeak.hotel.config;

import com.skypeak.hotel.entity.Role;
import com.skypeak.hotel.entity.User;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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

        Role adminRole = roleRepository.findByName(roleAdmin)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        User userAdmin = new User();
        userAdmin.setEmail(email);
        userAdmin.setPassword(password);
        userAdmin.setStatus(status);
        userAdmin.setRole(adminRole);

        userRepository.save(userAdmin);
        log.info("Admin with email {} created successfully", email);
    }

    private void addRole() {
        List<String> roleList = Stream.of(roleAdmin, roleManager, roleUser).toList();
        for (String roleName : roleList) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Role {} created", roleName);
            } else {
                log.info("Role {} already exists", roleName);
            }
        }
    }
}
