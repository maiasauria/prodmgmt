package com.alkemy.mleon.prodmgmt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Document(collection="products")
@Data //setters getters etc.
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
    private boolean inStock;
    private Set<Color> colors;

}
