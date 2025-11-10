package com.cosmocats.marketplace.config.features;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "feature")
public record FeatureToggles(
        CosmoCats cosmoCats,
        KittyProducts kittyProducts
) {

    /** Вкладений 'record' для 'feature.cosmoCats' */
    public record CosmoCats(
            boolean enabled
    ) {}

    /** Вкладений 'record' для 'feature.kittyProducts' */
    public record KittyProducts(
            boolean enabled
    ) {}
}
