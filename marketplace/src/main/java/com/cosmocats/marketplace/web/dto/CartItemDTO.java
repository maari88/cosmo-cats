package com.cosmocats.marketplace.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private String productName;
    private Double pricePerUnit;
}

