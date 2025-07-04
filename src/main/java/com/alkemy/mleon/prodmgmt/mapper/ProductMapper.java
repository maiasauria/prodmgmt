package com.alkemy.mleon.prodmgmt.mapper;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.enums.Color;
import com.alkemy.mleon.prodmgmt.model.Product;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default ProductDto toDTO(Product product) {
        if (product == null) {
            return null;
        }

        Set<String> colors = null;
        if (product.getColors() != null) {
            colors = product.getColors().stream()
                    .map(Color::name) // Convierte el enum a su representación en String
                    .collect(Collectors.toSet());
        }

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .inStock(product.isInStock())
                .colors(colors)
                .build();
    }

    default Product toEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        Set<Color> colors = null;
        if (productDto.getColors() != null) {
            colors = productDto.getColors().stream()
                    .map(color -> Color.valueOf(color.toUpperCase())) // Convierte el String a su representación en enum
                    .collect(Collectors.toSet());
        }

        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .inStock(productDto.isInStock())
                .colors(colors)
                .build();
    }

}