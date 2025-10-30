package com.cosmocats.marketplace.domain.service;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.exception.ProductNotFoundException;
import com.cosmocats.marketplace.web.dto.ProductCreateDTO;
import com.cosmocats.marketplace.web.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

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

        Product productToStore = Product.builder()
                .name("Starship Fuel")
                .price(100.0)
                .sku("SKU-FUEL-01")
                .build();

        when(productMapper.toEntity(dto)).thenReturn(productToStore);

        // Act
        Product createdProduct = productService.createProduct(dto);

        // Assert
        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId()); // Перевіряємо, що ID згенерувався
        assertEquals(1L, createdProduct.getId()); // Перший ID
        assertEquals("Starship Fuel", createdProduct.getName());
        assertEquals("SKU-FUEL-01", createdProduct.getSku());

        verify(productMapper, times(1)).toEntity(dto);

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
        Product product = Product.builder().name("Starship Fuel").build();
        when(productMapper.toEntity(dto)).thenReturn(product);

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
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains(String.valueOf(nonExistentId)));
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
        when(productMapper.toEntity(any(ProductCreateDTO.class)))
                .thenReturn(Product.builder().name("Product 1").build())
                .thenReturn(Product.builder().name("Product 2").build());

        productService.createProduct(new ProductCreateDTO());
        productService.createProduct(new ProductCreateDTO());

        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Product 1", products.get(0).getName());
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

        Product existingProduct = Product.builder()
                .name("Old Name").price(100.0).sku("SKU-123").build();

        when(productMapper.toEntity(createDto)).thenReturn(existingProduct);
        productService.createProduct(createDto);
        Long id = existingProduct.getId();

        ProductCreateDTO updateDto = new ProductCreateDTO();
        updateDto.setName("New Name");
        updateDto.setPrice(200.0);

        doAnswer(invocation -> {
            ProductCreateDTO dtoArg = invocation.getArgument(0);
            Product productArg = invocation.getArgument(1);

            if (dtoArg.getName() != null) productArg.setName(dtoArg.getName());
            if (dtoArg.getPrice() != null) productArg.setPrice(dtoArg.getPrice());
            return null; // void
        }).when(productMapper).updateFromDto(any(ProductCreateDTO.class), any(Product.class));


        // Act
        Product updatedProduct = productService.updateProductById(id, updateDto);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(id, updatedProduct.getId());
        assertEquals("New Name", updatedProduct.getName()); // Поле оновилось
        assertEquals(200.0, updatedProduct.getPrice()); // Поле оновилось
        assertEquals("SKU-123", updatedProduct.getSku()); // Поле *не* оновилось (бо в DTO його не було)

        verify(productMapper, times(1)).updateFromDto(updateDto, existingProduct);
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

        verify(productMapper, never()).updateFromDto(any(), any());
    }


    // -------------------
    // DELETE TESTS
    // -------------------

    @Test
    void deleteProductById_WhenProductExists_ShouldDeleteProduct() {
        // Arrange
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("To Be Deleted");
        Product product = Product.builder().name("To Be Deleted").build();
        when(productMapper.toEntity(dto)).thenReturn(product);

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