package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.config.features.FeatureToggles;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    private final FeatureToggles features;

    public FeatureToggleService(FeatureToggles features) {
        this.features = features;
    }

    public boolean isCosmoCatsEnabled() {
        return features.cosmoCats() != null && features.cosmoCats().enabled();
    }

    public boolean isKittyProductsEnabled() {
        return features.kittyProducts() != null && features.kittyProducts().enabled();
    }
}
