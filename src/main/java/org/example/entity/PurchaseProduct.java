package org.example.entity;

import jakarta.persistence.*;

@SuppressWarnings({"checkstyle:MissingJavadocType", "checkstyle:Indentation"})
@Entity
public class PurchaseProduct {
    @EmbeddedId
    private PurchaseProductPk ppId;

    private int quantity;

    private double price;

    private double weight;

    public PurchaseProductPk getPpId() {
        return ppId;
    }

    public void setPpId(PurchaseProductPk ppId) {
        this.ppId = ppId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
