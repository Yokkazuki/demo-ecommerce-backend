package com.ecommerce.demo.controller;

import com.ecommerce.demo.entity.Coupon;
import com.ecommerce.demo.repository.CouponRepository;
import com.ecommerce.demo.service.discount.DiscountStrategy;
import com.ecommerce.demo.service.discount.DiscountStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;
    private final DiscountStrategyFactory discountStrategyFactory;

    @GetMapping("/validate")
    public ResponseEntity<?> validate(
            @RequestParam String code,
            @RequestParam BigDecimal amount) {

        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Invalid coupon code"));

        if (!coupon.isAvailable()) {
            throw new RuntimeException("Coupon is not available");
        }

        if (coupon.getMinPurchase() != null
                && amount.compareTo(coupon.getMinPurchase()) < 0) {
            throw new RuntimeException(
                    "Minimum purchase amount is " + coupon.getMinPurchase());
        }

        DiscountStrategy strategy = discountStrategyFactory
                .getStrategy(coupon.getDiscountType());
        BigDecimal discount = strategy.calculateDiscount(coupon, amount);

        // 用 HashMap 替代 Map.of()，因為 HashMap 允許 null 值
        Map<String, Object> result = new HashMap<>();
        result.put("code", coupon.getCode());
        result.put("discountType", coupon.getDiscountType().name());
        result.put("discountValue", coupon.getDiscountValue());
        result.put("minPurchase", coupon.getMinPurchase());
        result.put("maxDiscount", coupon.getMaxDiscount());
        result.put("discount", discount);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon create(@RequestBody Coupon coupon) {
        coupon.setUsedCount(0);
        return couponRepository.save(coupon);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Coupon update(@PathVariable Long id, @RequestBody Coupon data) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        coupon.setCode(data.getCode());
        coupon.setDiscountType(data.getDiscountType());
        coupon.setDiscountValue(data.getDiscountValue());
        coupon.setMinPurchase(data.getMinPurchase());
        coupon.setMaxDiscount(data.getMaxDiscount());
        coupon.setQuantity(data.getQuantity());
        coupon.setIsActive(data.getIsActive());
        coupon.setExpireAt(data.getExpireAt());
        return couponRepository.save(coupon);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}