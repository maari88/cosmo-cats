package com.cosmocats.marketplace.web.dto;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double pricePerUnit;
    private Double lineTotal;
}

