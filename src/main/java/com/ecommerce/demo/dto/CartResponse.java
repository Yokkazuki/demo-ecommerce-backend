package com.ecommerce.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;     // 總金額
    private Integer totalItems;         // 商品種類數
}