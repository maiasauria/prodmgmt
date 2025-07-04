package com.alkemy.mleon.prodmgmt.controller;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.service.ProductService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @ApiResponse(responseCode = "201", description = "Consulta exitosa")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    public ResponseEntity<List<ProductDto>> listAllProducts() {
        log.info("ProductController: listAllProducts | 🛍️ Listando productos");
        List<ProductDto> products = productService.listProducts();
        return ResponseEntity.ok(products);
    }
// Lo dejo comentado porque no pude hacer que funcione correctamente
//    @GetMapping("/async")
//    @ApiResponse(responseCode = "201", description = "Consulta exitosa")
//    @ApiResponse(responseCode = "400", description = "Solicitud inválida")
//    public CompletableFuture<ResponseEntity<List<ProductDto>>> listAllProductsAsync() {
//        log.info("ProductController: listAllProductsAsync | 🛍️ Listando productos");
//        return productService.listProductsAsync()
//                .thenApply(products -> ResponseEntity.ok(products));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        ProductDto product = productService.getById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProductDto> getProductByName(@PathVariable String name) {
        ProductDto product = productService.getByName(name);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.createProduct(productDto); // Assuming createProduct can also handle updates
        return ResponseEntity.ok(updatedProduct);
    }
}
