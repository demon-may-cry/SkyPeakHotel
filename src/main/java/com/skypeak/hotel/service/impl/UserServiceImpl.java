package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.user.ChangeRoleRequest;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.repository.RoleRepository;
import com.skypeak.hotel.repository.UserRepository;
import com.skypeak.hotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
        log.info("Getting all users with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        log.info("Getting user by ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public void changeUserRole(UUID userId, ChangeRoleRequest request) {
        log.info("Changing role for user ID: {} to {}", userId, request.role().name());
        var user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: " + userId));

        var currentRole = user.getRoleEntity().getName();
        log.info("Current role for user ID: {} is {}", userId, currentRole);

        if (currentRole.equals(Role.ADMIN.name()))
            throw new IllegalArgumentException("Administrator role cannot be changed");

        if (currentRole.equals(request.role().name()))
            throw new IllegalArgumentException("User already has the role: " + request.role().name());

        var roleEntity = roleRepository.findByName(request.role().name()).orElseThrow(() ->
                new EntityNotFoundException("Role not found: " + request.role().name()));

        user.setRoleEntity(roleEntity);

        log.info("Role changed for user ID: {} to {}", userId, request.role().name());

    }

    @Override
    public void deactivateUser(UUID userId) {
        log.info("Deactivating user with ID: {}", userId);
        var user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: " + userId));

        if (user.getRoleEntity().getName().equals(Role.ADMIN.name()))
            throw new IllegalArgumentException("Administrator status cannot be changed");

        if (user.getStatus().equals("INACTIVE"))
            throw new IllegalArgumentException("User is already inactive");

        user.setStatus("INACTIVE");
        log.info("User with ID: {} has been deactivated", userId);

    }
}
