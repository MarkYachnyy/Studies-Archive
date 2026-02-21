package ru.vsu.cs.iachnyi_m_a.java.service;

import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItemId;
import ru.vsu.cs.iachnyi_m_a.java.repository.CartRepository;
import ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db.CartRepositoryIMDB;

import java.util.List;

public class CartService {
    private CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public CartItem getCartItemByUserIdAndProductId(Long userId, Long productId) {
        return repository.findById(new CartItemId(userId, productId)).orElse(null);
    }

    public List<CartItem> getCartOfUser(Long userId){
        return repository.findAllByUserId(userId);
    }

    public void deleteCartItem(CartItemId cartItemId){
        repository.delete(cartItemId);
    }

    public CartItem saveCartItem(CartItem cartItem){
        return repository.save(cartItem);
    }
}
