package com.alkemy.mleon.prodmgmt.dto;

import com.alkemy.mleon.prodmgmt.model.Color;
import lombok.Data;

import java.util.Set;

@Data
public class ProductDto {
    private String id;
    private String name;
    private Double price;
    private boolean inStock;
    private Set<Color> colors;


}
