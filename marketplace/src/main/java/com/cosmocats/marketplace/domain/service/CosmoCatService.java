package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.config.features.Feature;
import com.cosmocats.marketplace.config.features.RequiresFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatService {

    private static final Logger log = LoggerFactory.getLogger(CosmoCatService.class);

    @RequiresFeature(Feature.COSMO_CATS)
    public List<String> getCosmoCats() {
        // Runnable
        log.info("ACCESS CONFIRMED: Download cosmo-cat list...");
        return List.of("Captain Meow", "General Purr", "New");
    }

    @RequiresFeature(Feature.KITTY_PRODUCTS)
    public List<String> getSpecialKittyProducts() {
        // Will never run
        log.warn("LOGIC ERROR: That code isn`t runnable!");
        return List.of("Cosmic leash", "Laser point");
    }
}
