package com.cosmocats.marketplace.external;

import com.cosmocats.marketplace.external.dto.ExternalPriceDTO;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// ⬇️ 1. ДОДАНО НОВИЙ ІМПОРТ
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@DisabledIfEnvironmentVariable(
        named = "CI",
        matches = "true",
        disabledReason = "WireMock server fails to start in GitHub Actions"
)
public class PricingClientWiremockTest {

    @Autowired
    private PricingClient pricingClient;

    static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMockServer.resetAll();
    }


    @DynamicPropertySource
    static void wiremockProperties(DynamicPropertyRegistry registry) {
        registry.add("pricing.api.base-url",
                () -> "http://localhost:" + wireMockServer.port());
    }

    @Test
    void getExternalPriceForSku_WhenProductExists_ShouldReturnPrice() {
        // --- 1. Arrange (Stubbing) ---
        stubFor(get(urlEqualTo("/api/v1/prices/SKU-123"))
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

        verify(getRequestedFor(urlEqualTo("/api/v1/prices/SKU-123")));
    }

    @Test
    void getExternalPriceForSku_WhenProductNotFound_ShouldReturnEmpty() {
        // --- 1. Arrange (Stubbing) ---
        stubFor(get(urlEqualTo("/api/v1/prices/SKU-404"))
                .willReturn(aResponse().withStatus(404)));

        // --- 2. Act ---
        Optional<ExternalPriceDTO> response = pricingClient.getExternalPriceForSku("SKU-404");

        // --- 3. Assert ---
        assertTrue(response.isEmpty());
        verify(getRequestedFor(urlEqualTo("/api/v1/prices/SKU-404")));
    }

    @Test
    void getExternalPriceForSku_WhenServerReturnsError_ShouldReturnEmpty() {
        // --- 1. Arrange (Stubbing) ---
        stubFor(get(urlEqualTo("/api/v1/prices/SKU-500"))
                .willReturn(aResponse().withStatus(500)));

        // --- 2. Act ---
        Optional<ExternalPriceDTO> response = pricingClient.getExternalPriceForSku("SKU-500");

        // --- 3. Assert ---
        assertTrue(response.isEmpty());
    }
}