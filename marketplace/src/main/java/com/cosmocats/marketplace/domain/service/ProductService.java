package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.entity.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.domain.repository.ProductRepository;
import com.cosmocats.marketplace.domain.repository.projection.TopProductProjection;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.cosmocats.marketplace.web.mapper.ProductMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    // -------------------
    // CREATE
    // -------------------
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(ProductCreateDTO dto) {
        Product product = productMapper.toEntity(dto);
        return productRepository.save(product);
    }

    // -------------------
    // READ
    // -------------------
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<TopProductProjection> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }

    // -------------------
    // UPDATE
    // -------------------
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProductById(Long productId, ProductCreateDTO dto) {
        Product existing = getProductById(productId);
        productMapper.updateFromDto(dto, existing);
        return existing;
    }

    // -------------------
    // DELETE
    // -------------------
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProductById(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        } else {
            throw new ProductNotFoundException(productId);
        }
    }
}