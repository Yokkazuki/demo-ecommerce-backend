package com.ecommerce.demo.controller;
import com.ecommerce.demo.dto.AddToCartRequest;
import com.ecommerce.demo.dto.CartResponse;
import com.ecommerce.demo.dto.UpdateCartItemRequest;
import com.ecommerce.demo.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 取得當前用戶的購物車
    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cartService.getCart(username));
    }

    // 加入商品
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        String username = authentication.getName();
        CartResponse cart = cartService.addToCart(username, request);
        return ResponseEntity.ok(cart);
    }

    // 修改數量
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        String username = authentication.getName();
        CartResponse cart = cartService.updateCartItem(username, itemId, request);
        return ResponseEntity.ok(cart);
    }

    // 移除商品
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            Authentication authentication,
            @PathVariable Long itemId) {
        String username = authentication.getName();
        CartResponse cart = cartService.removeCartItem(username, itemId);
        return ResponseEntity.ok(cart);
    }
}