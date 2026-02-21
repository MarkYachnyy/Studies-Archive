package ru.vsu.cs.iachnyi_m_a.java.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private long id;
    private long sellerId;
    private String name;
    private int price;
    private int stockQuantity;

    public Product(Product product1) {
        this.id = product1.getId();
        this.sellerId = product1.getSellerId();
        this.name = product1.getName();
        this.price = product1.getPrice();
        this.stockQuantity = product1.getStockQuantity();
    }
}
