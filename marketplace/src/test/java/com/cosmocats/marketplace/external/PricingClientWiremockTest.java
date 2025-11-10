package com.cosmocats.marketplace.external;

import com.cosmocats.marketplace.external.dto.ExternalPriceDTO;
import com.cosmocats.marketplace.config.WireMockInitializer;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
public class PricingClientWiremockTest {

    @Autowired
    private PricingClient pricingClient;

    private final WireMockServer wireMockServer = WireMockInitializer.wireMockServer;


    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }

    @Test
    void getExternalPriceForSku_WhenProductExists_ShouldReturnPrice() {
        // --- 1. Arrange (Stubbing) ---
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/prices/SKU-123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                """
                                {
                                    "sku": "SKU-123",
                                    "price": 199.99,
                                    "currency": "USD"
                                }
                                """)
                ));

        // --- 2. Act ---
        Optional<ExternalPriceDTO> response = pricingClient.getExternalPriceForSku("SKU-123");

        // --- 3. Assert ---
        assertTrue(response.isPresent());
        assertEquals(new BigDecimal("199.99"), response.get().price());
        assertEquals("SKU-123", response.get().sku());

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/prices/SKU-123")));
    }

    @Test
    void getExternalPriceForSku_WhenProductNotFound_ShouldReturnEmpty() {
        // --- 1. Arrange (Stubbing) ---
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/prices/SKU-404"))
                .willReturn(aResponse().withStatus(404)));

        // --- 2. Act ---
        Optional<ExternalPriceDTO> response = pricingClient.getExternalPriceForSku("SKU-404");

        // --- 3. Assert ---
        assertTrue(response.isEmpty());
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/prices/SKU-404")));
    }

    @Test
    void getExternalPriceForSku_WhenServerReturnsError_ShouldReturnEmpty() {
        // --- 1. Arrange (Stubbing) ---
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/prices/SKU-500"))
                .willReturn(aResponse().withStatus(500)));

        // --- 2. Act ---
        Optional<ExternalPriceDTO> response = pricingClient.getExternalPriceForSku("SKU-500");

        // --- 3. Assert ---
        assertTrue(response.isEmpty());
    }
}