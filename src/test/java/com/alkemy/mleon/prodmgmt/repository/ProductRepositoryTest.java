package com.alkemy.mleon.prodmgmt.repository;

import com.alkemy.mleon.prodmgmt.enums.Color;
import com.alkemy.mleon.prodmgmt.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryUnitTest {

    @Mock
    private ProductRepository productRepository;  // Mocking the ProductRepository

    private Product createTestProduct() {
        return Product.builder()
                .id("1")
                .name("Test Product")
                .inStock(true)
                .price(100.0)
                .colors(Set.of(Color.WHITE))
                .build();
    }

    @Test
    void findByName_shouldReturnProductWhenExists() {
        Product product = createTestProduct();
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productRepository.findByName("Test Product");

        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
        verify(productRepository).findByName("Test Product");
    }

    @Test
    void findByName_shouldReturnEmptyWhenNotExists() {
        when(productRepository.findByName("Nonexistent Product")).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productRepository.findByName("Nonexistent Product");

        assertFalse(foundProduct.isPresent());
        verify(productRepository).findByName("Nonexistent Product");
    }

}