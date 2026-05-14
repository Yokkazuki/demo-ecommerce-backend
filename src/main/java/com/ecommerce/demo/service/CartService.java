package com.ecommerce.demo.service;
import com.ecommerce.demo.dto.*;
import com.ecommerce.demo.entity.*;
import com.ecommerce.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 取得或建立用戶的購物車
    private Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    // 查看購物車
    public CartResponse getCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });

        // 直接查詢該購物車的所有 CartItem
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        return convertToCartResponse(cart, items);
    }

    // 加入商品到購物車
    @Transactional
    public CartResponse addToCart(String username, AddToCartRequest request) {
        Cart cart = getOrCreateCart(username);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 檢查庫存
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        // 檢查購物車是否已有該商品
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            // 已存在：增加數量
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            // 不存在：新增
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        // 重新載入 Cart（避免一級快取問題）
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return convertToCartResponse(cart, items);
    }

    // 修改購物車商品數量
    @Transactional
    public CartResponse updateCartItem(String username, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(username);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to current user");
        }

        if (item.getProduct().getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + item.getProduct().getStock());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        // 🔧 重新載入 Cart
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return convertToCartResponse(cart, items);
    }

    // 移除購物車商品
    @Transactional
    public CartResponse removeCartItem(String username, Long itemId) {
        Cart cart = getOrCreateCart(username);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to current user");
        }

        cartItemRepository.delete(item);

        // 🔧 重新載入 Cart
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        return convertToCartResponse(cart, items);
    }

    // 清空購物車（下單後會用到）
    @Transactional
    public void clearCart(String username) {
        Cart cart = getOrCreateCart(username);
        cartItemRepository.deleteAll(cart.getItems());
    }

    // 轉換為 CartResponse DTO
    private CartResponse convertToCartResponse(Cart cart, List<CartItem> items) {
        List<CartItemDTO> itemDTOs = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : items) {
            Product product = item.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            CartItemDTO dto = CartItemDTO.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build();

            itemDTOs.add(dto);
            totalAmount = totalAmount.add(subtotal);
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemDTOs)
                .totalAmount(totalAmount)
                .totalItems(itemDTOs.size())
                .build();
    }
}