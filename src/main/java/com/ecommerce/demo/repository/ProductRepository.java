package com.ecommerce.demo.repository;

import com.ecommerce.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stock > 0 ORDER BY p.id ASC")
    Page<Product> findActiveProducts(Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stock > 0 AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.id ASC")
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p ORDER BY p.id ASC")
    Page<Product> findAllWithPage(Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.id ASC")
    Page<Product> searchAll(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT DISTINCT p FROM Product p JOIN p.tags t WHERE p.isActive = true AND p.stock > 0 AND t.id = :tagId ORDER BY p.id ASC")
    Page<Product> findByTagId(@Param("tagId") Long tagId, Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    @Query("SELECT DISTINCT p FROM Product p JOIN p.tags t WHERE p.isActive = true AND p.stock > 0 AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.id = :tagId ORDER BY p.id ASC")
    Page<Product> searchByKeywordAndTag(@Param("keyword") String keyword, @Param("tagId") Long tagId, Pageable pageable);
    
    long countByIsActiveTrue();
}