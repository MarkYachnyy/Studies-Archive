package ru.vsu.cs.iachnyi_m_a.java.repository;

import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItemId;

import java.util.List;

public interface CartRepository extends DataRepository<CartItem, CartItemId> {
    List<CartItem> findAllByUserId(long userId);
}
