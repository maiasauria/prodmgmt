package com.alkemy.mleon.prodmgmt.service.impl;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.mapper.ProductMapper;
import com.alkemy.mleon.prodmgmt.model.Product;
import com.alkemy.mleon.prodmgmt.repository.ProductRepository;
import com.alkemy.mleon.prodmgmt.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    // Inyección de dependencias
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ExecutorService executorService =
            new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(5));


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
    public CompletableFuture<List<ProductDto>> listProductsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Usuario autenticado: {}", SecurityContextHolder.getContext().getAuthentication().getName());
            log.info("Listando productos en segundo plano");
            log.info("Roles del usuario: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            return productRepository.findAll()
                    .stream()
                    .map(productMapper::toDTO)
                    .toList();
        }, executorService).exceptionally(ex -> {
            log.error("Error al listar productos: {}", ex.getMessage());
            throw new RuntimeException("Error al listar productos", ex);
        });
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

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        // Asumiendo que el ID del producto está presente en el DTO
        if (productDto.getId() == null || !productRepository.existsById(productDto.getId())) {
            throw new RuntimeException("Product not found");
        }
        Product product = productMapper.toEntity(productDto);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toDTO(updatedProduct);
    }

}
