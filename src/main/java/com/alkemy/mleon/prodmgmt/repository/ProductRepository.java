package com.alkemy.mleon.prodmgmt.repository;

import com.alkemy.mleon.prodmgmt.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {
    Optional<Product> findByName(String name);
}
