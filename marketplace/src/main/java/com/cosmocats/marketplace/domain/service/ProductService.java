package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.domain.repository.ProductRepository;
import com.cosmocats.marketplace.domain.repository.projection.TopProductProjection;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.cosmocats.marketplace.web.mapper.ProductMapper;
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
    public Product createProduct(ProductCreateDTO dto) {
        Product product = productMapper.toEntity(dto);
        return productRepository.save(product);
    }

    // -------------------
    // READ
    // -------------------
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public List<TopProductProjection> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }

    // -------------------
    // UPDATE
    // -------------------
    @Transactional
    public Product updateProductById(Long productId, ProductCreateDTO dto) {
        Product existing = getProductById(productId);
        productMapper.updateFromDto(dto, existing);
        return existing;
    }

    // -------------------
    // DELETE
    // -------------------
    @Transactional
    public void deleteProductById(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        }
    }
}