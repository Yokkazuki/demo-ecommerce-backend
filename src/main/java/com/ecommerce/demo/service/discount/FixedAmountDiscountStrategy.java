package com.ecommerce.demo.service.discount;

import com.ecommerce.demo.entity.Coupon;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class FixedAmountDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal totalAmount) {
        // 固定金額折扣（但不能超過訂單總額）
        BigDecimal discount = coupon.getDiscountValue();
        if (discount.compareTo(totalAmount) > 0) {
            discount = totalAmount;
        }
        return discount;
    }
}