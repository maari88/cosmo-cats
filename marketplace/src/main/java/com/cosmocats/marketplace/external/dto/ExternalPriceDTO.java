package com.cosmocats.marketplace.external.dto;

import java.math.BigDecimal;

public record ExternalPriceDTO(
        String sku,
        BigDecimal price,
        String currency
) {
}