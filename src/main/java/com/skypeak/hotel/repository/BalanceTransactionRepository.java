package com.skypeak.hotel.repository;

import com.skypeak.hotel.entity.BalanceTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
public interface BalanceTransactionRepository extends JpaRepository<BalanceTransactionEntity, UUID> {
}