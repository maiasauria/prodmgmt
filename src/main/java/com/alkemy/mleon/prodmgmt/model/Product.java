package com.alkemy.mleon.prodmgmt.model;

import com.alkemy.mleon.prodmgmt.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Document(collection="products")
@Data //setters getters etc.
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
public class Product {
    @Id
    private String id;

    private String name;

    private Double price;

    private boolean inStock;

    private Set<Color> colors;

}
