package com.cosmocats.marketplace.web.controller;

import com.cosmocats.marketplace.config.SecurityConfig;
import com.cosmocats.marketplace.domain.entity.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.domain.service.ProductService;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerStandaloneTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductCreateDTO validDto;
    private Product productStub;

    @BeforeEach
    void setUp() {
        validDto = new ProductCreateDTO();
        validDto.setName("Galaxy-Class Starship");
        validDto.setSku("SKU-GXY-001");
        validDto.setPrice(100000.99);
        validDto.setCurrency("USD");
        validDto.setStock(10);

        productStub = Product.builder()
                .id(1L)
                .name("Galaxy-Class Starship")
                .sku("SKU-GXY-001")
                .price(100000.99)
                .build();
    }

    // --- CREATE  ---

    @Test
    @DisplayName("Should create product when authorized as ADMIN")
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void createProduct_WhenValid_ShouldReturn201Created() throws Exception {
        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productStub);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("http://localhost/api/v1/products/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Galaxy-Class Starship")));
    }

    @Test
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void createProduct_WhenNameIsBlank_ShouldReturn400BadRequest() throws Exception {
        validDto.setName("");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.invalid-params[0].field", is("name")));
    }

    @Test
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void createProduct_WhenSkuPatternIsInvalid_ShouldReturn400BadRequest() throws Exception {
        validDto.setSku("invalid sku!");

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.invalid-params[0].field", is("sku")));
    }

    @Test
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void createProduct_WhenPriceIsZero_ShouldReturn400BadRequest() throws Exception {
        validDto.setPrice(0.0);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-params[0].field", is("price")));
    }

    // --- READ  ---

    @Test
    @DisplayName("Should return 200 OK and list of products (Authorized as User)")
    @WithMockUser(username = "cosmo-user", roles = {"USER"})
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productStub));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    @WithMockUser(username = "cosmo-user", roles = {"USER"})
    void getProductById_WhenProductExists_ShouldReturn200OK() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productStub);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @WithMockUser(username = "cosmo-user", roles = {"USER"})
    void getProductById_WhenProductNotFound_ShouldReturn404ProblemDetail() throws Exception {
        when(productService.getProductById(999L)).thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title", is("Product Not Found")))
                .andExpect(jsonPath("$.productId", is(999)));
    }

    // --- UPDATE/DELETE  ---

    @Test
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void updateProductById_WhenValid_ShouldReturn200OK() throws Exception {
        when(productService.updateProductById(eq(1L), any(ProductCreateDTO.class))).thenReturn(productStub);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void updateProductById_WhenNameIsBlank_ShouldReturn400BadRequest() throws Exception {
        validDto.setName(null);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-params[0].field", is("name")));
    }

    @Test
    @WithMockUser(username = "cosmo-admin", roles = {"ADMIN"})
    void deleteProductById_ShouldReturn204NoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "cosmo-user")
    void getProductById_WhenUnexpectedError_ShouldReturn500ProblemDetail() throws Exception {
        when(productService.getProductById(any(Long.class)))
                .thenThrow(new RuntimeException("Unexpected database error"));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title", is("Internal Server Error")))
                .andExpect(jsonPath("$.detail", is("An unexpected error occurred. Please contact support.")));
    }
}