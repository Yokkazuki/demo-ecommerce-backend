package com.ecommerce.demo.controller;

import com.ecommerce.demo.dto.OrderResponse;
import com.ecommerce.demo.entity.OrderStatus;
import com.ecommerce.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ==================== 前台 ====================

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication,
            @RequestParam(required = false) String couponCode) {
        String username = authentication.getName();
        OrderResponse order = orderService.createOrder(username, couponCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.getMyOrders(username));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.getOrderById(username, orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId) {
        String username = authentication.getName();
        return ResponseEntity.ok(orderService.cancelOrder(username, orderId));
    }

    // ==================== 後台 ====================

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponse> getAllOrders(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAllOrders(keyword, status, PageRequest.of(page, size));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, newStatus));
    }
}