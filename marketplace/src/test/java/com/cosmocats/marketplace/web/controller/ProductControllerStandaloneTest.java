package com.cosmocats.marketplace.web.controller;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.domain.service.ProductService;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.cosmocats.marketplace.web.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerStandaloneTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper DTO
    private ProductCreateDTO validDto;
    private Product productStub;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();

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

    // --- CREATE (POST) TESTS ---

    @Test
    void createProduct_WhenValid_ShouldReturn201Created() throws Exception {
        // Arrange
        when(productService.createProduct(any(ProductCreateDTO.class))).thenReturn(productStub);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated()) // 201
                .andExpect(header().string("Location", startsWith("http://localhost/api/v1/products/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Galaxy-Class Starship")));
    }

    @Test
    void createProduct_WhenNameIsBlank_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validDto.setName("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.invalid-params[0].field", is("name")));
    }

    @Test
    void createProduct_WhenSkuPatternIsInvalid_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validDto.setSku("invalid sku!");

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.invalid-params[0].field", is("sku")));
    }

    @Test
    void createProduct_WhenPriceIsZero_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validDto.setPrice(0.0);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-params[0].field", is("price")));
    }

    // --- READ (GET) TESTS ---

    @Test
    void getProductById_WhenProductExists_ShouldReturn200OK() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(productStub);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldReturn404ProblemDetail() throws Exception {
        // Arrange
        when(productService.getProductById(999L)).thenThrow(new ProductNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound()) // 404
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title", is("Product Not Found")))
                .andExpect(jsonPath("$.productId", is(999)));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(List.of(productStub));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    // --- UPDATE (PUT) TESTS ---

    @Test
    void updateProductById_WhenValid_ShouldReturn200OK() throws Exception {
        // Arrange
        when(productService.updateProductById(eq(1L), any(ProductCreateDTO.class))).thenReturn(productStub);

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void updateProductById_WhenNameIsBlank_ShouldReturn400BadRequest() throws Exception {
        // Arrange
        validDto.setName(null); // Порушення валідації

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid-params[0].field", is("name")));
    }

    // --- DELETE (DELETE) TESTS ---

    @Test
    void deleteProductById_ShouldReturn204NoContent() throws Exception {
        // Arrange (нічого не мокаємо, сервіс повертає void)

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent()); // 204
    }
}