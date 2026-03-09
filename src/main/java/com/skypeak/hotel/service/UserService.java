package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.user.ChangeRoleRequest;
import com.skypeak.hotel.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface UserService {

    Page<UserEntity> findAll(Pageable pageable);

    UserEntity getUserById(UUID userId);

    void changeUserRole(UUID userId, ChangeRoleRequest request);

    void deactivateUser(UUID userId);
}
