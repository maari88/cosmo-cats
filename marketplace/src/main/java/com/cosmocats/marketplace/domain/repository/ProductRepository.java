package com.cosmocats.marketplace.domain.repository;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.repository.projection.TopProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);

    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description, Pageable pageable);
    @Query("SELECT p.name AS name, SUM(oi.quantity) AS totalSold " +
            "FROM OrderItem oi " +
            "JOIN oi.product p " +
            "GROUP BY p.id, p.name " +
            "ORDER BY totalSold DESC")
    List<TopProductProjection> findTopSellingProducts();
}

