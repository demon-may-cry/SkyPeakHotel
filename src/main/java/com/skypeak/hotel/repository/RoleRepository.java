package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Дмитрий Ельцов
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(@Size(max = 30) @NotNull Role name);
}