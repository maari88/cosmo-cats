package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @AfterEach
    void tearDown() {
        productService.clearStore();
    }


    // -------------------
    // CREATE TESTS
    // -------------------

    @Test
    void createProduct_ShouldMapDtoToEntityAndStoreProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Starship Fuel");
        dto.setPrice(100.0);
        dto.setSku("SKU-FUEL-01");
        dto.setCurrency("USD");
        dto.setStock(1);

        // Act
        Product createdProduct = productService.createProduct(dto);

        // Assert
        assertNotNull(createdProduct);
        assertEquals(1L, createdProduct.getId());
        assertEquals("Starship Fuel", createdProduct.getName());
        assertEquals("SKU-FUEL-01", createdProduct.getSku());

        Product foundProduct = productService.getProductById(createdProduct.getId());
        assertEquals(createdProduct, foundProduct);
    }

    // -------------------
    // READ TESTS
    // -------------------

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Starship Fuel");
        dto.setSku("SKU-TEST");
        dto.setPrice(10.0);
        dto.setCurrency("USD");
        dto.setStock(1);
        Product createdProduct = productService.createProduct(dto);

        // Act
        Product foundProduct = productService.getProductById(createdProduct.getId());

        // Assert
        assertNotNull(foundProduct);
        assertEquals(createdProduct.getId(), foundProduct.getId());
        assertEquals("Starship Fuel", foundProduct.getName());
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(nonExistentId)
        );
    }

    @Test
    void getAllProducts_WhenNoProducts_ShouldReturnEmptyList() {
        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void getAllProducts_WhenProductsExist_ShouldReturnAllProducts() {
        // Arrange
        ProductCreateDTO dto1 = new ProductCreateDTO();
        dto1.setName("Product 1");
        dto1.setSku("SKU-1");
        dto1.setPrice(10.0);
        dto1.setCurrency("USD");
        dto1.setStock(1);

        ProductCreateDTO dto2 = new ProductCreateDTO();
        dto2.setName("Product 2");
        dto2.setSku("SKU-2");
        dto2.setPrice(20.0);
        dto2.setCurrency("USD");
        dto2.setStock(1);

        productService.createProduct(dto1);
        productService.createProduct(dto2);

        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    // -------------------
    // UPDATE TESTS
    // -------------------

    @Test
    void updateProductById_WhenProductExists_ShouldUpdateProduct() {
        // Arrange
        ProductCreateDTO createDto = new ProductCreateDTO();
        createDto.setName("Old Name");
        createDto.setPrice(100.0);
        createDto.setSku("SKU-123");
        createDto.setCurrency("USD");
        createDto.setStock(10);
        Product createdProduct = productService.createProduct(createDto);
        Long id = createdProduct.getId();

        ProductCreateDTO updateDto = new ProductCreateDTO();
        updateDto.setName("New Name");
        updateDto.setPrice(200.0);

        // Act
        Product updatedProduct = productService.updateProductById(id, updateDto);

        // Assert
        assertEquals("New Name", updatedProduct.getName());
        assertEquals(200.0, updatedProduct.getPrice());
        assertEquals("SKU-123", updatedProduct.getSku());
    }

    @Test
    void updateProductById_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        // Arrange
        Long nonExistentId = 999L;
        ProductCreateDTO updateDto = new ProductCreateDTO();
        updateDto.setName("New Name");

        // Act & Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProductById(nonExistentId, updateDto)
        );
    }

    // -------------------
    // DELETE TESTS
    // -------------------

    @Test
    void deleteProductById_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("To Be Deleted");
        dto.setSku("SKU-DEL");
        dto.setPrice(10.0);
        dto.setCurrency("USD");
        dto.setStock(1);
        Product createdProduct = productService.createProduct(dto);
        Long id = createdProduct.getId();

        assertNotNull(productService.getProductById(id));

        // Act
        productService.deleteProductById(id);

        // Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(id));
    }

    @Test
    void deleteProductById_WhenProductNotFound_ShouldNotThrowException() {
        // Arrange
        Long nonExistentId = 999L;

        // Act & Assert
        assertDoesNotThrow(() -> productService.deleteProductById(nonExistentId));
    }
}