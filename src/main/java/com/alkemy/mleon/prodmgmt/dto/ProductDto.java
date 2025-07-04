package com.alkemy.mleon.prodmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ProductDto {

    @Schema(description = "ID único del producto", example = "12345")
    private String id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Nombre del producto", example = "Laptop")
    private String name;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio del producto", example = "999.99")
    private Double price;

    @Schema(description = "Indica si el producto está en stock", example = "true")
    private boolean inStock;

    @NotNull(message = "Los colores no pueden ser nulos")
    @Schema(description = "Colores disponibles del producto", example = "[\"RED\", \"BLUE\"]")
    private Set<String> colors;
}