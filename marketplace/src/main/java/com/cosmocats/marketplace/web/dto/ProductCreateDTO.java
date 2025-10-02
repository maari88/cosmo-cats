package com.cosmocats.marketplace.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class ProductCreateDTO {
    @NotBlank
    @Size(min = 5, max = 64)
    @Pattern(regexp = "^[A-Z0-9\\-]+$")
    private String sku;

    @NotBlank
    @Size(min = 3, max = 200)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double price;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}$")
    private String currency;

    private String originPlanet;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private Double antigravityCoefficient;

    @Min(0) @Max(5)
    private Integer radiationRating;

    @NotNull
    @Min(0)
    private Integer stock;

    private Double weightKg;
    private Set<String> tags;
    private Long categoryId;
}
