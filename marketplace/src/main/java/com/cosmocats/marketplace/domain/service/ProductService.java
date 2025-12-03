package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.cosmocats.marketplace.web.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final Map<Long, Product> productStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    // -------------------
    // CREATE
    // -------------------
    public Product createProduct(ProductCreateDTO dto) {
        Product product = productMapper.toEntity(dto);

        long id = idGenerator.getAndIncrement();
        product.setId(id);

        productStore.put(id, product);
        return product;
    }

    // -------------------
    // READ
    // -------------------
    public List<Product> getAllProducts() {
        return new ArrayList<>(productStore.values());
    }

    // READ - single
    public Product getProductById(Long productId) {
        Product product = productStore.get(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        return product;
    }

    // -------------------
    // UPDATE
    // -------------------
    public Product updateProductById(Long productId, ProductCreateDTO dto) {
        Product existing = productStore.get(productId);
        if (existing == null) {
            throw new ProductNotFoundException(productId);
        }
        productMapper.updateFromDto(dto, existing);
        productStore.put(productId, existing);
        return existing;
    }

    // -------------------
    // DELETE
    // -------------------
    public void deleteProductById(Long productId) {
        productStore.remove(productId);
    }

    public void clearStore() {
        productStore.clear();
        idGenerator.set(1);
    }

}

