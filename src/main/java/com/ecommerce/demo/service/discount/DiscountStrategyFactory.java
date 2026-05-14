package com.ecommerce.demo.service.discount;

import com.ecommerce.demo.entity.DiscountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscountStrategyFactory {

    private final PercentageDiscountStrategy percentageStrategy;
    private final FixedAmountDiscountStrategy fixedAmountStrategy;

    public DiscountStrategy getStrategy(DiscountType discountType) {
        return switch (discountType) {
            case PERCENTAGE -> percentageStrategy;
            case FIXED_AMOUNT -> fixedAmountStrategy;
        };
    }
}