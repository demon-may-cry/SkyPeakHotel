package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Role;
import com.skypeak.hotel.entity.enums.Status;
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
    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(Pageable pageable) {
        log.info("Getting all users with pagination: page={}, size={}",
                pageable.getPageNumber(),
                pageable.getPageSize());
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserById(UUID id) {
        log.info("Getting user by ID: {}", id);
        return getUserOrThrow(id);
    }

    @Override
    public void changeUserRole(UUID id, Role request) {
        log.info("Changing role for user ID: {} to {}",
                id,
                request.name());
        var user = getUserOrThrow(id);

        var currentRole = user.getRole().getName();
        log.info("Current role for user ID: {} is {}", id, currentRole);

        if (currentRole == Role.ADMIN)
            throw new IllegalArgumentException("Administrator role cannot be changed");

        if (currentRole == request)
            throw new IllegalArgumentException("User already has the role: " + request.name());

        var role = roleRepository.findByName(request).orElseThrow(() ->
                new EntityNotFoundException("Role not found: " + request.name()));

        user.setRole(role);

        log.info("Role changed for user ID: {} from {} to {}",
                id,
                currentRole,
                request.name());
    }

    @Override
    public void deactivateUser(UUID id) {
        log.info("Deactivating user with ID: {}", id);
        var user = getUserOrThrow(id);

        if (user.getRole().getName().equals(Role.ADMIN))
            throw new IllegalArgumentException("Administrator status cannot be changed");

        if (user.getStatus() == Status.INACTIVE)
            throw new IllegalArgumentException("User is already inactive");

        user.setStatus(Status.INACTIVE);
        log.info("User with ID: {} has been deactivated", id);

    }

    @Override
    public void activateUser(UUID id) {
        log.info("Activating user with ID: {}", id);
        var user = getUserOrThrow(id);

        if (user.getStatus() == Status.ACTIVE)
            throw new IllegalArgumentException("User is already active");

        user.setStatus(Status.ACTIVE);
        log.info("User with ID: {} has been activated", id);
    }

    private UserEntity getUserOrThrow(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found with ID: " + id));
    }
}
