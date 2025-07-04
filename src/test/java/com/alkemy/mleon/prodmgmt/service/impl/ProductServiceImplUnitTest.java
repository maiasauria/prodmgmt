package com.alkemy.mleon.prodmgmt.service.impl;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.mapper.ProductMapper;
import com.alkemy.mleon.prodmgmt.model.Product;
import com.alkemy.mleon.prodmgmt.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest {

        @Mock
        private ProductRepository productRepository;

        @Mock
        private ProductMapper productMapper;

        @InjectMocks
        private ProductServiceImpl productService;

        private static final String PRODUCT_ID = "456";

        private static final Product PRODUCT = Product.builder()
                .id(PRODUCT_ID)
                .name("Producto Test")
                .price(100.0)
                .inStock(true)
                .build();

        private static final ProductDto PRODUCT_DTO = ProductDto.builder()
                .id(PRODUCT_ID)
                .name("Producto Test")
                .price(100.0)
                .inStock(true)
                .build();

        @Test
        void getAllProducts_shouldReturnListOfProductDTO() {
            when(productRepository.findAll()).thenReturn(List.of(PRODUCT));
            when(productMapper.toDTO(PRODUCT)).thenReturn(PRODUCT_DTO);

            List<ProductDto> products = productService.listProducts();

            assertEquals(1, products.size());
            assertEquals("Producto Test", products.get(0).getName());
            verify(productRepository).findAll();
        }

        @Test
        void createProduct_shouldSaveAndReturnProductDTO() {
            when(productMapper.toEntity(PRODUCT_DTO)).thenReturn(PRODUCT);
            when(productRepository.save(PRODUCT)).thenReturn(PRODUCT);
            when(productMapper.toDTO(PRODUCT)).thenReturn(PRODUCT_DTO);

            ProductDto created = productService.createProduct(PRODUCT_DTO);

            assertNotNull(created);
            assertEquals("Producto Test", created.getName());
            verify(productRepository).save(PRODUCT);
        }


        @Test
        void deleteProduct_whenExists_shouldDeleteProduct() {
            when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);

            productService.deleteProduct(PRODUCT_ID);

            verify(productRepository).deleteById(PRODUCT_ID);
        }

        @Test
        void deleteProduct_whenNotExists_shouldThrowException() {
            when(productRepository.existsById(PRODUCT_ID)).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> productService.deleteProduct(PRODUCT_ID));

            assertEquals("Product not found", ex.getMessage());
            verify(productRepository).existsById(PRODUCT_ID);
        }
    }