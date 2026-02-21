package ru.vsu.cs.iachnyi_m_a.java.entity.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private CartItemId id;
    private int quantity;

    public CartItem(CartItem cartItem1) {
        id = new CartItemId(cartItem1.getId());
        quantity = cartItem1.getQuantity();
    }
}
