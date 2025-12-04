package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.domain.repository.ProductRepository;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

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
        dto.setStock(10);

        // Act
        Product createdProduct = productService.createProduct(dto);

        // Assert
        assertNotNull(createdProduct);

        assertNotNull(createdProduct.getId());

        assertEquals("Starship Fuel", createdProduct.getName());
        assertEquals("SKU-FUEL-01", createdProduct.getSku());

        Product foundProduct = productService.getProductById(createdProduct.getId());
        assertEquals(createdProduct.getId(), foundProduct.getId());
    }

    // -------------------
    // READ TESTS
    // -------------------

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("Starship Fuel");
        dto.setSku("SKU-TEST-READ");
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
        Long nonExistentId = 9999L;

        // Act & Assert
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(nonExistentId)
        );
    }

    @Test
    void getAllProducts_WhenNoProducts_ShouldReturnEmptyList() {
        productRepository.deleteAll();

        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void getAllProducts_WhenProductsExist_ShouldReturnAllProducts() {
        // Arrange
        productRepository.deleteAll();

        ProductCreateDTO dto1 = new ProductCreateDTO();
        dto1.setName("Product 1");
        dto1.setSku("SKU-LIST-1");
        dto1.setPrice(10.0);
        dto1.setCurrency("USD");
        dto1.setStock(1);

        ProductCreateDTO dto2 = new ProductCreateDTO();
        dto2.setName("Product 2");
        dto2.setSku("SKU-LIST-2");
        dto2.setPrice(20.0);
        dto2.setCurrency("USD");
        dto2.setStock(1);

        productService.createProduct(dto1);
        productService.createProduct(dto2);

        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
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
        createDto.setSku("SKU-UPD-1");
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
        assertEquals("SKU-UPD-1", updatedProduct.getSku());
    }

    @Test
    void updateProductById_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        // Arrange
        Long nonExistentId = 9999L;
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
        dto.setSku("SKU-DEL-1");
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
        Long nonExistentId = 9999L;

        // Act & Assert
        assertDoesNotThrow(() -> productService.deleteProductById(nonExistentId));
    }
}