package com.ecommerce.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;                // 優惠券代碼，例如 "WELCOME50"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;  // 折扣類型

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;   // 折扣值

    @Column(name = "min_purchase", precision = 10, scale = 2)
    private BigDecimal minPurchase;     // 最低消費金額

    @Column(name = "max_discount", precision = 10, scale = 2)
    private BigDecimal maxDiscount;     // 最高折抵金額

    @Column(nullable = false)
    private Integer quantity;           // 總發行量

    @Column(name = "used_count")
    @Builder.Default
    private Integer usedCount = 0;      // 已使用數量

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;     // 過期時間

    public boolean isAvailable() {
        return isActive
                && usedCount < quantity
                && (expireAt == null || expireAt.isAfter(LocalDateTime.now()));
    }
}