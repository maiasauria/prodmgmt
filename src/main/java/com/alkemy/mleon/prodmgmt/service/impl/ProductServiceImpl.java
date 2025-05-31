package com.alkemy.mleon.prodmgmt.service.impl;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.mapper.ProductMapper;
import com.alkemy.mleon.prodmgmt.model.Product;
import com.alkemy.mleon.prodmgmt.repository.ProductRepository;
import com.alkemy.mleon.prodmgmt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    // Inyección de dependencias
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDto getById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<ProductDto> listProducts() {
        return productRepository
                .findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }


    @Override
    public ProductDto getByName(String nombre) {
        return productRepository.findByName(nombre)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }


}
