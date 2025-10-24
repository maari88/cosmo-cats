package com.cosmocats.marketplace.web.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Double price;
    private String currency;
    private String originPlanet;
    private Double antigravityCoefficient;
    private Integer radiationRating;
    private Integer stock;
    private Double weightKg;
    private Set<String> tags;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private CategoryDTO category;
}

