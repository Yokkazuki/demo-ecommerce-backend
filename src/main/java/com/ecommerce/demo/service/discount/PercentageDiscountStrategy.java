package com.ecommerce.demo.service.discount;

import com.ecommerce.demo.entity.Coupon;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentageDiscountStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal totalAmount) {
        // 計算百分比折扣
        BigDecimal discount = totalAmount.multiply(coupon.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 不能超過最高折抵金額
        if (coupon.getMaxDiscount() != null
                && discount.compareTo(coupon.getMaxDiscount()) > 0) {
            discount = coupon.getMaxDiscount();
        }

        return discount;
    }
}