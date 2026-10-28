//
//
//package com.aquanest.category;
//
//import java.util.List;
//import java.util.Optional;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface CategoryRepository
//        extends JpaRepository<Category, Long> {
//
//    List<Category> findAllByActiveTrueOrderByNameAsc();
//
//    Optional<Category> findByNameIgnoreCase(String name);
//
//    @Repository
//    default long countByActiveTrue() {
//        return 0;
//    }
//
//}







package com.aquanest.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByActiveTrueOrderByNameAsc();

    Optional<Category> findByNameIgnoreCase(String name);

    long countByActiveTrue();
}
