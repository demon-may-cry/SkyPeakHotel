package com.skypeak.hotel.util;

import com.skypeak.hotel.entity.RoleEntity;
import com.skypeak.hotel.entity.enums.Role;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/**
 * @author Дмитрий Ельцов
 */
public class TestFixtures {

    public static RoleEntity getOrCreateRole(TestEntityManager em, Role name) {

        RoleEntity role = em.getEntityManager()
                .createQuery("select r from RoleEntity r where r.name = :name", RoleEntity.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (role == null) {
            role = new RoleEntity();
            role.setName(name);
            em.persistAndFlush(role);
        }

        return role;
    }

}
