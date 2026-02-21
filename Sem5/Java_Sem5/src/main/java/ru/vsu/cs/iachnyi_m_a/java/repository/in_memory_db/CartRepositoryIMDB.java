package ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db;

import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItemId;
import ru.vsu.cs.iachnyi_m_a.java.repository.CartRepository;

import java.util.List;
import java.util.Optional;

public class CartRepositoryIMDB implements CartRepository {

    private InMemoryDatabase database;

    public CartRepositoryIMDB(InMemoryDatabase database) {
        this.database = database;
    }

    @Override
    public List<CartItem> findAllByUserId(long userId) {
        return database.getAllCartItems().stream().filter(ci -> ci.getId().getUserId() == userId).toList();
    }

    @Override
    public Optional<CartItem> findById(CartItemId id) {
        return database.getAllCartItems().stream().filter(cartItem -> cartItem.getId().equals(id)).findFirst();
    }

    @Override
    public List<CartItem> findAll() {
        return database.getAllCartItems();
    }

    @Override
    public CartItem save(CartItem entity) {
        if(findById(entity.getId()).isPresent()) {
            return database.updateCartItem(entity);
        } else {
            return database.insertCartItem(entity);
        }
    }

    @Override
    public void delete(CartItemId id) {
        database.deleteCartItem(id);
    }
}
