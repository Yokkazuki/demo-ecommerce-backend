package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ProductDTO;
import com.ecommerce.demo.entity.Product;
import com.ecommerce.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    private static final String PRODUCT_KEY = "product:";
    private static final long CACHE_TTL = 30;

    public Optional<ProductDTO> getProductById(Long productId) {
        String key = PRODUCT_KEY + productId;

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            // 強制轉換 LinkedHashMap → ProductDTO
            ProductDTO dto = objectMapper.convertValue(cached, ProductDTO.class);
            return Optional.of(dto);
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        productOpt.ifPresent(product -> {
            ProductDTO dto = productService.convertToDTO(product);
            redisTemplate.opsForValue().set(key, dto, CACHE_TTL, TimeUnit.MINUTES);
        });

        return productOpt.map(productService::convertToDTO);
    }

    public void evictProduct(Long productId) {
        redisTemplate.delete(PRODUCT_KEY + productId);
    }
}