package com.cosmocats.marketplace.config.features;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "feature")
public record FeatureToggles(
        CosmoCats cosmoCats,
        KittyProducts kittyProducts
) {

    public record CosmoCats(
            boolean enabled
    ) {}

    public record KittyProducts(
            boolean enabled
    ) {}
}
