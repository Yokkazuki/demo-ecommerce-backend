package com.ecommerce.demo.controller;

import com.ecommerce.demo.dto.ProductDTO;
import com.ecommerce.demo.entity.Product;
import com.ecommerce.demo.service.ProductCacheService;
import com.ecommerce.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductCacheService productCacheService;

    @GetMapping
    public Page<ProductDTO> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return productService.getAllProducts(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public Page<ProductDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        return productService.searchProducts(keyword, tagId, PageRequest.of(page, size));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProductDTO> getAllProductsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProductsAdmin(PageRequest.of(page, size));
    }

    @GetMapping("/search/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProductDTO> searchProductsAdmin(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProductsAdmin(keyword, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productCacheService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        productCacheService.evictProduct(id);
        Product updated = productService.updateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productCacheService.evictProduct(id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}