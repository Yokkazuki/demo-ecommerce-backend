package com.ecommerce.demo.service.discount;

import com.ecommerce.demo.entity.Coupon;
import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculateDiscount(Coupon coupon, BigDecimal totalAmount);
}