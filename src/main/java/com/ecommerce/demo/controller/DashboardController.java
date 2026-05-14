package com.ecommerce.demo.controller;

import com.ecommerce.demo.entity.OrderStatus;
import com.ecommerce.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 總商品數
        stats.put("totalProducts", productRepository.count());
        // 上架商品數
        stats.put("activeProducts", productRepository.countByIsActiveTrue());
        // 總訂單數
        stats.put("totalOrders", orderRepository.count());
        // 總用戶數
        stats.put("totalUsers", userRepository.count());
        // 總優惠券數
        stats.put("totalCoupons", couponRepository.count());

        // 總營收
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatusNot(OrderStatus.CANCELLED);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // 訂單狀態統計
        List<Object[]> statusCounts = orderRepository.countByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("orderStatusCounts", statusMap);

        return ResponseEntity.ok(stats);
    }
}