package com.ecommerce.demo.service;

import com.ecommerce.demo.dto.ProductDTO;
import com.ecommerce.demo.dto.TagDTO;
import com.ecommerce.demo.entity.Product;
import com.ecommerce.demo.entity.Tag;
import com.ecommerce.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findActiveProducts(pageable);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products.getContent()) {
            dtos.add(convertToDTO(product));
        }
        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Long tagId, Pageable pageable) {
        Page<Product> products;
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasTag = tagId != null;

        if (hasKeyword && hasTag) {
            products = productRepository.searchByKeywordAndTag(keyword, tagId, pageable);
        } else if (hasKeyword) {
            products = productRepository.searchActiveProducts(keyword, pageable);
        } else if (hasTag) {
            products = productRepository.findByTagId(tagId, pageable);
        } else {
            products = productRepository.findActiveProducts(pageable);
        }

        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products.getContent()) {
            dtos.add(convertToDTO(product));
        }
        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProductsAdmin(Pageable pageable) {
        Page<Product> products = productRepository.findAllWithPage(pageable);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products.getContent()) {
            dtos.add(convertToDTO(product));
        }
        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsAdmin(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.searchAll(keyword, pageable);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : products.getContent()) {
            dtos.add(convertToDTO(product));
        }
        return new PageImpl<>(dtos, pageable, products.getTotalElements());
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setImageUrl(productDetails.getImageUrl());
        product.setIsActive(productDetails.getIsActive());
        if (productDetails.getTags() != null) {
            product.setTags(productDetails.getTags());
        }
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductDTO convertToDTO(Product product) {
        List<TagDTO> tagDTOs = new ArrayList<>();
        if (product.getTags() != null) {
            for (Tag tag : product.getTags()) {
                tagDTOs.add(new TagDTO(tag.getId(), tag.getName(), tag.getColor()));
            }
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .tags(tagDTOs)
                .build();
    }
}