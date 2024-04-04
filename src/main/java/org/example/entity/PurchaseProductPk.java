package org.example.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Embeddable
public class PurchaseProductPk implements Serializable {
    private Long productId;
    private Long purchaseId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PurchaseProductPk purchaseProductPk = (PurchaseProductPk) o;
        return Objects.equals(productId, purchaseProductPk.productId) && Objects.equals(purchaseId, purchaseProductPk.purchaseId);
    }
}
