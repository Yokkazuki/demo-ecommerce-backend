package com.ecommerce.demo.repository;

import com.ecommerce.demo.entity.Order;
import com.ecommerce.demo.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Optional<Order> findById(Long id);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    Page<Order> findAllWithPage(Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status <> :status")
    BigDecimal sumTotalAmountByStatusNot(@Param("status") OrderStatus status);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    // 動態查詢：只有關鍵字
    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    @Query("SELECT o FROM Order o WHERE CAST(o.id AS string) LIKE %:keyword% ORDER BY o.createdAt DESC")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 動態查詢：只有狀態
    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // 動態查詢：關鍵字 + 狀態
    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    @Query("SELECT o FROM Order o WHERE CAST(o.id AS string) LIKE %:keyword% AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") OrderStatus status, Pageable pageable);
}