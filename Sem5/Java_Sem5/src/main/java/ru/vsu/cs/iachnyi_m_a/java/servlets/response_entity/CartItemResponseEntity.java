package ru.vsu.cs.iachnyi_m_a.java.servlets.response_entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponseEntity {
    private long productId;
    private String productName;
    private int quantity;
    private int price;
}
