package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.entity.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.domain.repository.ProductRepository;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.cosmocats.marketplace.web.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser; 
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@WithMockUser(username = "service-test-admin", roles = {"ADMIN", "USER"})
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductMapper productMapper;

    @Test
    @DisplayName("createProduct_ShouldMapDtoToEntityAndStoreProduct")
    void createProduct_ShouldMapDtoToEntityAndStoreProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Test Product");

        Product productEntity = new Product();
        productEntity.setName("Test Product");

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Test Product");

        when(productMapper.toEntity(dto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(savedProduct);

        // Act
        Product result = productService.createProduct(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).save(productEntity);
    }

    @Test
    @DisplayName("getProductById_WhenProductExists_ShouldReturnProduct")
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getProductById_WhenProductNotFound_ShouldThrowProductNotFoundException")
    void getProductById_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    @DisplayName("getAllProducts_WhenNoProducts_ShouldReturnEmptyList")
    void getAllProducts_WhenNoProducts_ShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = productService.getAllProducts();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getAllProducts_WhenProductsExist_ShouldReturnAllProducts")
    void getAllProducts_WhenProductsExist_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("updateProductById_WhenProductExists_ShouldUpdateProduct")
    void updateProductById_WhenProductExists_ShouldUpdateProduct() {
        Long id = 1L;
        ProductCreateDTO dto = new ProductCreateDTO();
        Product existingProduct = new Product();
        existingProduct.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));

        // Mock mapper update
        doAnswer(invocation -> {
            return null;
        }).when(productMapper).updateFromDto(dto, existingProduct);

        Product result = productService.updateProductById(id, dto);

        assertThat(result).isNotNull();
        verify(productMapper).updateFromDto(dto, existingProduct);
    }

    @Test
    @DisplayName("updateProductById_WhenProductNotFound_ShouldThrowProductNotFoundException")
    void updateProductById_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProductById(1L, new ProductCreateDTO()));
    }

    @Test
    @DisplayName("deleteProductById_WhenProductExists_ShouldDeleteProduct")
    void deleteProductById_WhenProductExists_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProductById(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProductById_WhenProductNotFound_ShouldThrowException")
    void deleteProductById_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(1L));
    }
}