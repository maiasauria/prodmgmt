package com.alkemy.mleon.prodmgmt.service.impl;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.repository.ProductRepository;
import com.alkemy.mleon.prodmgmt.service.ProductService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceImplIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    static String createdProductId;

    @Test
    @Order(1)
    void createProduct_shouldPersistProduct() {
        ProductDto productDto = ProductDto.builder()
                .name("Product Test")
                .price(100.0)
                .inStock(true)
                .build();

        ProductDto savedProduct = productService.createProduct(productDto);
        createdProductId = savedProduct.getId();

        assertThat(savedProduct.getId()).isNotBlank();
        assertThat(savedProduct.getName()).isEqualTo("Product Test");
        assertThat(productRepository.existsById(createdProductId)).isTrue();
    }

    @Test
    @Order(2)
    void getAllProducts_shouldReturnListWithAtLeastOneProduct() {
        List<ProductDto> products = productService.listProducts();
        assertThat(products).isNotEmpty();
    }


    @Test
    @Order(3)
    void deleteProduct_shouldRemoveProduct() {
        productService.deleteProduct(createdProductId);
        assertThat(productRepository.findById(createdProductId)).isNotPresent();
    }

    @Test
    @Order(4)
    void deleteProduct_shouldThrowWhenProductNotFound() {
        assertThatThrownBy(() -> productService.deleteProduct("nonexistentId"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }
}
