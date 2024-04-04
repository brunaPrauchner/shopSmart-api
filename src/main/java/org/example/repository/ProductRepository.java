package org.example.repository;

import org.example.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
