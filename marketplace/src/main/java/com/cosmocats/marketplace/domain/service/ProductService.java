package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
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

    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productStore.get(id));
    }

    // -------------------
    // UPDATE
    // -------------------
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        Product existing = productStore.get(id);
        if (existing == null) {
            return Optional.empty();
        }

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setCategory(updatedProduct.getCategory());
        existing.setSku(updatedProduct.getSku());

        productStore.put(id, existing);
        return Optional.of(existing);
    }

    // -------------------
    // DELETE
    // -------------------
    public boolean deleteProduct(Long id) {
        return productStore.remove(id) != null;
    }

}

