package org.example.repository;

import org.example.entity.PurchaseProduct;
import org.example.entity.PurchaseProductPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Repository
public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, PurchaseProductPk> {

}

