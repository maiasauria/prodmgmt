package com.alkemy.mleon.prodmgmt.service;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ProductDto productDto);

    ProductDto getById(String id);

    List<ProductDto> listProducts();

    void deleteProduct(String id);

    ProductDto getByName(String nombre);

    ProductDto updateProduct(ProductDto productDto); // Assuming you want to add an update method
}
