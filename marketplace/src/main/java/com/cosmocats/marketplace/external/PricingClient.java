package com.cosmocats.marketplace.external;

import com.cosmocats.marketplace.external.dto.ExternalPriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class PricingClient {

    private static final Logger log = LoggerFactory.getLogger(PricingClient.class);
    private final RestClient restClient;

    public PricingClient(RestClient.Builder restClientBuilder,
                         @Value("${pricing.api.base-url}") String baseUrl) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public Optional<ExternalPriceDTO> getExternalPriceForSku(String sku) {
        log.info("Requesting external price for SKU: {}", sku);
        try {
            ExternalPriceDTO response = restClient.get()
                    .uri("/api/v1/prices/{sku}", sku)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ExternalPriceDTO.class);

            return Optional.ofNullable(response);

        } catch (HttpClientErrorException.NotFound notFound) {
            log.warn("No external price found for SKU: {}", sku);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching external price for SKU: {}", sku, e);
            return Optional.empty();
        }
    }
}
