package org.example.repository;

import org.example.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
