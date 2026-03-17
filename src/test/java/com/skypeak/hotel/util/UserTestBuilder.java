package com.skypeak.hotel.util;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.entity.enums.Status;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * @author Дмитрий Ельцов
 */
public class UserTestBuilder {

    private final TestEntityManager entityManager;

    private String email;
    private String password;
    private RoleEntity role;
    private Status status;

    public UserTestBuilder(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public UserTestBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserTestBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserTestBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public UserTestBuilder role(RoleEntity roleName) {
        this.role = roleName;
        return this;
    }

    public UserEntity build() {

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(status);

        return entityManager.persistAndFlush(user);
    }
}
