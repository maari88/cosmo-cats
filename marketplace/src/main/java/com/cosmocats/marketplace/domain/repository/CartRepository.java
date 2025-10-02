package com.cosmocats.marketplace.domain.repository;

import com.cosmocats.marketplace.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByOwnerId(String ownerId);
}

