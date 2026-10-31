package com.aquanest.order;

import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT o
        FROM CustomerOrder o
        WHERE o.id = :id
        """)
    Optional<CustomerOrder> findByIdForUpdate(
            @Param("id") Long id
    );
}