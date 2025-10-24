package com.cosmocats.marketplace.web.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private String ownerId; // customer id або session id
    private List<CartItemDTO> items;
    private OffsetDateTime updatedAt;

    private Integer totalItems;
    private Double totalEstimated;
    private String currency;
}

