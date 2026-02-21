package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.cart.CartItemId;
import ru.vsu.cs.iachnyi_m_a.java.repository.CartRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartRepositorySQL implements CartRepository {

    private DatabaseConnectionPool pool;

    public CartRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public List<CartItem> findAllByUserId(long userId) {
        List<CartItem> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM cartitems WHERE userId = ?");
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new CartItem(new CartItemId(resultSet.getLong("userId"), resultSet.getLong("productId")), resultSet.getInt("quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public Optional<CartItem> findById(CartItemId id) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM cartitems WHERE userId = ? AND productId = ?");
            statement.setLong(1, id.getUserId());
            statement.setLong(2, id.getProductId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new CartItem(new CartItemId(resultSet.getLong("userId"), resultSet.getLong("productId")), resultSet.getInt("quantity")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return Optional.empty();
    }

    @Override
    public List<CartItem> findAll() {
        return List.of();
    }

    @Override
    public CartItem save(CartItem entity) {
        Connection connection = pool.retrieve();
        try{
            CartItem existing = findById(entity.getId()).orElse(null);
            PreparedStatement statement;
            if(existing != null){
                statement = connection.prepareStatement("UPDATE cartitems SET quantity = ? WHERE userid = ? AND productid = ?");
                statement.setInt(1, entity.getQuantity());
                statement.setLong(2, entity.getId().getUserId());
                statement.setLong(3, entity.getId().getProductId());
            } else {
                statement = connection.prepareStatement("INSERT INTO cartitems (userid, productid, quantity) VALUES (?, ?, ?)");
                statement.setInt(3, entity.getQuantity());
                statement.setLong(1, entity.getId().getUserId());
                statement.setLong(2, entity.getId().getProductId());
            }
            statement.executeUpdate();
            return new CartItem(entity);
        } catch (SQLException e){
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }

        return null;
    }

    @Override
    public void delete(CartItemId id) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement;
            statement = connection.prepareStatement("DELETE FROM cartitems WHERE userid = ? AND productid = ?");
            statement.setLong(1, id.getUserId());
            statement.setLong(2, id.getProductId());
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
    }
}
