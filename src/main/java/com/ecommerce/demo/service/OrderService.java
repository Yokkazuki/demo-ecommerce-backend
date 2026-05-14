package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.*;
import com.ecommerce.demo.entity.*;
import com.ecommerce.demo.repository.*;
import com.ecommerce.demo.service.discount.DiscountStrategy;
import com.ecommerce.demo.service.discount.DiscountStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final DiscountStrategyFactory discountStrategyFactory;

    // ==================== 前台 ====================

    @Transactional
    public OrderResponse createOrder(String username, String couponCode) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (product.getStock() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            Coupon coupon = couponRepository.findByCode(couponCode.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

            if (!coupon.isAvailable()) {
                throw new RuntimeException("Coupon is not available");
            }

            if (coupon.getMinPurchase() != null && totalAmount.compareTo(coupon.getMinPurchase()) < 0) {
                throw new RuntimeException("Minimum purchase amount is " + coupon.getMinPurchase());
            }

            DiscountStrategy strategy = discountStrategyFactory.getStrategy(coupon.getDiscountType());
            discount = strategy.calculateDiscount(coupon, totalAmount);
            totalAmount = totalAmount.subtract(discount);

            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToOrderResponse(savedOrder);
    }

    public OrderResponse createOrder(String username) {
        return createOrder(username, null);
    }

    public OrderResponse getOrderById(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        return convertToOrderResponse(order);
    }

    public List<OrderResponse> getMyOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            responses.add(convertToOrderResponse(order, items));
        }
        return responses;
    }

    @Transactional
    public OrderResponse cancelOrder(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be cancelled");
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<OrderItem> savedItems = orderItemRepository.findByOrderId(orderId);
        return convertToOrderResponse(order, savedItems);
    }

    // ==================== 後台 ====================

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(String keyword, String status, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasStatus = status != null && !status.isEmpty();

        Page<Order> orders;
        if (hasKeyword && hasStatus) {
            orders = orderRepository.findByKeywordAndStatus(keyword, OrderStatus.valueOf(status), pageable);
        } else if (hasKeyword) {
            orders = orderRepository.findByKeyword(keyword, pageable);
        } else if (hasStatus) {
            orders = orderRepository.findByStatus(OrderStatus.valueOf(status), pageable);
        } else {
            orders = orderRepository.findAllWithPage(pageable);
        }

        return orders.map(order -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            return convertToOrderResponse(order, items);
        });
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        return convertToOrderResponse(order, items);
    }

    // ==================== DTO 轉換 ====================

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return convertToOrderResponse(order, items);
    }

    private OrderResponse convertToOrderResponse(Order order, List<OrderItem> items) {
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        for (OrderItem item : items) {
            OrderItemDTO dto = OrderItemDTO.builder()
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .build();
            itemDTOs.add(dto);
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .username(order.getUser().getUsername())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .build();
    }
}