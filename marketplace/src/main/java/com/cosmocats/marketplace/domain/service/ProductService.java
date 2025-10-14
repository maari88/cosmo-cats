package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final Map<Long, Product> productStore = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // -------------------
    // CREATE
    // -------------------
    public Product createProduct(Product product) {
        long id = idGenerator.getAndIncrement();
        product.setId(id);

        if (product.getSku() == null || product.getSku().isBlank()) {
            product.setSku("COSMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

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
    public Product updateProductById(Long productId, Product updated) {
        Product existing = productStore.get(productId);
        if (existing == null) {
            throw new ProductNotFoundException(productId);
        }

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setSku(updated.getSku());
        existing.setAntigravityCoefficient(updated.getAntigravityCoefficient());
        existing.setOriginPlanet(updated.getOriginPlanet());
        existing.setRadiationRating(updated.getRadiationRating());
        existing.setStock(updated.getStock());

        productStore.put(productId, existing);
        return existing;
    }

    // -------------------
    // DELETE
    // -------------------
    public void deleteProductById(Long productId) {
        productStore.remove(productId);
    }

}

