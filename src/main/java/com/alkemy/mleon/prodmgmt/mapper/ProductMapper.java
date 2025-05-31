package com.alkemy.mleon.prodmgmt.mapper;

import com.alkemy.mleon.prodmgmt.dto.ProductDto;
import com.alkemy.mleon.prodmgmt.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDTO(Product product);
    Product toEntity(ProductDto productDto);
}
