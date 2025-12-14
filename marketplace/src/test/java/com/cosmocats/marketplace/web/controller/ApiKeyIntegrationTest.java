package com.cosmocats.marketplace.web.controller;

import com.cosmocats.marketplace.domain.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiKeyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Should allow access with valid API Key")
    void shouldAllowAccess_WhenApiKeyIsValid() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("X-API-KEY", "COSMO-SUPER-SECRET-KEY-2025"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny access with invalid API Key")
    void shouldDenyAccess_WhenApiKeyIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                        .header("X-API-KEY", "WRONG-KEY"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid API Key"));
    }

    @Test
    @DisplayName("Should deny access without any authentication")
    void shouldDenyAccess_WhenNoAuthProvided() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isUnauthorized());
    }
}
