package org.example.repository;

import org.example.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
