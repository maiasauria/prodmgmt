package com.alkemy.mleon.prodmgmt.repository;

import com.alkemy.mleon.prodmgmt.enums.Color;
import com.alkemy.mleon.prodmgmt.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class ProductRepositoryIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        testProduct = Product.builder()
                .name("Test Product")
                .price(100.0)
                .inStock(true)
                .colors(Set.of(Color.WHITE, Color.BLACK))
                .build();

        productRepository.save(testProduct);
    }

    @Test
    void findByName_shouldReturnProduct_whenNameExists() {
        Optional<Product> foundProduct = productRepository.findByName("Test Product");
        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
    }

    @Test
    void findByName_shouldReturnEmptyOptional_whenNameDoesNotExist() {
        Optional<Product> foundProduct = productRepository.findByName("Nonexistent Product");
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void save_shouldCreateNewProduct() {
        Product newProduct = Product.builder()
                .name("New Product")
                .price(200.0)
                .inStock(false)
                .colors(Set.of(Color.GREEN))
                .build();

        Product savedProduct = productRepository.save(newProduct);

        assertNotNull(savedProduct.getId());
        assertEquals("New Product", savedProduct.getName());

        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals("New Product", retrievedProduct.get().getName());
    }

    @Test
    void findById_shouldReturnProduct_whenIdExists() {
        Optional<Product> foundProduct = productRepository.findById(testProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(testProduct.getName(), foundProduct.get().getName());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenIdDoesNotExist() {
        Optional<Product> foundProduct = productRepository.findById("nonexistent-id");
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void findAll_shouldReturnAllProducts() {
        Product anotherProduct = Product.builder()
                .name("Another Product")
                .price(150.0)
                .inStock(true)
                .colors(Set.of(Color.BLACK))
                .build();
        productRepository.save(anotherProduct);

        List<Product> products = productRepository.findAll();

        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Test Product")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Another Product")));
    }

    @Test
    void update_shouldModifyExistingProduct() {
        testProduct.setPrice(120.0);
        testProduct.setInStock(false);

        Product updatedProduct = productRepository.save(testProduct);

        assertEquals(testProduct.getId(), updatedProduct.getId());
        assertEquals(120.0, updatedProduct.getPrice());
        assertFalse(updatedProduct.isInStock());

        Optional<Product> retrievedProduct = productRepository.findById(testProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals(120.0, retrievedProduct.get().getPrice());
    }

    @Test
    void deleteById_shouldRemoveProduct() {
        productRepository.deleteById(testProduct.getId());

        Optional<Product> deletedProduct = productRepository.findById(testProduct.getId());
        assertFalse(deletedProduct.isPresent());
        assertEquals(0, productRepository.count());
    }

    @Test
    void count_shouldReturnNumberOfProducts() {
        Product anotherProduct = Product.builder()
                .name("Another Product")
                .price(150.0)
                .inStock(true)
                .colors(Set.of(Color.BLACK))
                .build();
        productRepository.save(anotherProduct);

        long count = productRepository.count();

        assertEquals(2, count);
    }
}