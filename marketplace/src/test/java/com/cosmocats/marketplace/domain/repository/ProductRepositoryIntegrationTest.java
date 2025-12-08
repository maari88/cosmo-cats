package com.cosmocats.marketplace.domain.repository;

import com.cosmocats.marketplace.AbstractIntegrationTest;
import com.cosmocats.marketplace.domain.Category;
import com.cosmocats.marketplace.domain.Order;
import com.cosmocats.marketplace.domain.OrderItem;
import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.domain.repository.projection.TopProductProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
class ProductRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setName("Space Food");
        testCategory.setCode("FOOD-001");
        testCategory = categoryRepository.save(testCategory);
    }


    @Test
    @DisplayName("Should save and retrieve product by ID")
    void saveAndFindById() {
        Product product = new Product();
        product.setName("Mars Bar");
        product.setSku("MARS-001");
        product.setPrice(5.50);
        product.setCategory(testCategory);
        product.setCurrency("USD");
        product.setStock(10);

        Product savedProduct = productRepository.save(product);

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Mars Bar");
    }

    @Test
    @DisplayName("Should find product by SKU")
    void findBySku() {
        Product product = new Product();
        product.setName("Venus Pie");
        product.setSku("VENUS-001");
        product.setPrice(12.0);
        product.setCategory(testCategory);
        product.setCurrency("USD");
        product.setStock(5);

        productRepository.save(product);

        Optional<Product> result = productRepository.findBySku("VENUS-001");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Venus Pie");
    }

    @Test
    @DisplayName("Should update product price")
    void updateProduct() {
        Product product = new Product();
        product.setName("Moon Cheese");
        product.setSku("MOON-001");
        product.setPrice(10.0);
        product.setCategory(testCategory);
        product.setCurrency("USD");
        product.setStock(20);

        Product saved = productRepository.save(product);

        saved.setPrice(20.0);
        Product updated = productRepository.save(saved);

        Optional<Product> retrieved = productRepository.findById(updated.getId());
        assertThat(retrieved.get().getPrice()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Should delete product")
    void deleteProduct() {
        Product product = new Product();
        product.setName("Comet Candy");
        product.setSku("COMET-001");
        product.setPrice(1.0);
        product.setCategory(testCategory);
        product.setCurrency("USD");
        product.setStock(100);

        Product saved = productRepository.save(product);

        productRepository.deleteById(saved.getId());

        Optional<Product> retrieved = productRepository.findById(saved.getId());
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should return top selling products via projection")
    void findTopSellingProducts() {
        Product p1 = new Product();
        p1.setName("Water");
        p1.setSku("H2O");
        p1.setPrice(1.0);
        p1.setCategory(testCategory);
        p1.setCurrency("USD");
        p1.setStock(100);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Air");
        p2.setSku("O2");
        p2.setPrice(0.0);
        p2.setCategory(testCategory);
        p2.setCurrency("USD");
        p2.setStock(100);
        productRepository.save(p2);

        Order order = new Order();
        order.setCustomerId("user1");
        order.setOrderNumber("ORD-111");
        order.setTotalAmount(100.0);
        order.setCurrency("USD");
        orderRepository.save(order);

        OrderItem item1 = new OrderItem();
        item1.setOrder(order);
        item1.setProduct(p1);
        item1.setQuantity(5);
        item1.setPricePerUnit(1.0);
        item1.setLineTotal(5.0);
        orderItemRepository.save(item1);

        OrderItem item2 = new OrderItem();
        item2.setOrder(order);
        item2.setProduct(p2);
        item2.setQuantity(2);
        item2.setPricePerUnit(0.0);
        item2.setLineTotal(0.0);
        orderItemRepository.save(item2);

        // Act
        List<TopProductProjection> topProducts = productRepository.findTopSellingProducts();

        // Assert
        assertThat(topProducts).hasSize(2);
        assertThat(topProducts.get(0).getName()).isEqualTo("Water");
        assertThat(topProducts.get(0).getTotalSold()).isEqualTo(5L);

        assertThat(topProducts.get(1).getName()).isEqualTo("Air");
        assertThat(topProducts.get(1).getTotalSold()).isEqualTo(2L);
    }
}