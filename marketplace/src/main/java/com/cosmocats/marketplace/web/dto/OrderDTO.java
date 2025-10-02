package com.cosmocats.marketplace.web.dto;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String customerId;
    private List<OrderItemDTO> items;
    private Double totalAmount;
    private String currency;
    private OffsetDateTime createdAt;
}

