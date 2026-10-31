//package com.aquanest.product;
//
//public class ProductRepository {
//}








package com.aquanest.product;

import java.util.Optional;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndActiveTrue(Long id);


    @Query(
            value = """
            SELECT p
            FROM Product p
            JOIN p.category c
            WHERE p.active = true
              AND c.active = true
              AND (
                :search = ''
                OR LOWER(p.name)
                   LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.description)
                   LIKE LOWER(CONCAT('%', :search, '%'))
              )
              AND (
                :category = ''
                OR LOWER(c.name) = LOWER(:category)
              )
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Product p
            JOIN p.category c
            WHERE p.active = true
              AND c.active = true
              AND (
                :search = ''
                OR LOWER(p.name)
                   LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.description)
                   LIKE LOWER(CONCAT('%', :search, '%'))
              )
              AND (
                :category = ''
                OR LOWER(c.name) = LOWER(:category)
              )
            """
    )
    Page<Product> searchActiveProducts(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p
    FROM Product p
    WHERE p.id = :id
    """)
    Optional<Product> findByIdForUpdate(
            @Param("id") Long id
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT p
    FROM Product p
    WHERE p.id IN :ids
    ORDER BY p.id
    """)
    List<Product> findAllByIdsForUpdate(
            @Param("ids") Collection<Long> ids
    );

    long countByActiveTrue();

    @Query("""
    SELECT COALESCE(SUM(p.stock), 0)
    FROM Product p
    WHERE p.active = true
    """)
    Long totalActiveStock();



}