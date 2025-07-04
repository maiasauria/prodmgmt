package com.alkemy.mleon.prodmgmt.controller;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.enums.Color;
import com.alkemy.mleon.prodmgmt.model.Product;
import com.alkemy.mleon.prodmgmt.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAll_shouldReturnProductsList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void create_shouldCreateProduct() throws Exception {
        ProductDto newProduct = ProductDto.builder()
                .name("New Product")
                .price(200.0)
                .inStock(false)
                .colors(Set.of("GREEN"))
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_shouldUpdateProduct() throws Exception {
        ProductDto updatedProduct = ProductDto.builder()
                .name("Updated Product")
                .price(150.0)
                .inStock(true)
                .colors(Set.of("BLACK"))
                .build();

        mockMvc.perform(put("/api/products/" + testProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void delete_shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/" + testProduct.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductByName_shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/name/" + testProduct.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(100.0));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProductById_shouldReturnProduct() throws Exception {
        mockMvc.perform(get("/api/products/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(100.0));
    }
}