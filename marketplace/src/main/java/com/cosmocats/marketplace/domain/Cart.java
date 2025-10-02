package com.cosmocats.marketplace.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerId; // customer id/session id

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> items;

    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = OffsetDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}

