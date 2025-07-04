package com.alkemy.mleon.prodmgmt.service;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    ProductDto getById(String id);

    List<ProductDto> listProducts();

    CompletableFuture<List<ProductDto>> listProductsAsync();

    void deleteProduct(String id);

    ProductDto getByName(String nombre);

    ProductDto updateProduct(ProductDto productDto);
}
