package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItemId;
import ru.vsu.cs.iachnyi_m_a.java.repository.OrderItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderItemRepositorySQL implements OrderItemRepository {

    private DatabaseConnectionPool pool;

    public OrderItemRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public List<OrderItem> findAllByOrderId(long orderId) {
        List<OrderItem> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM orderitems WHERE orderid = ?");
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new OrderItem(new OrderItemId(resultSet.getLong("orderId"), resultSet.getLong("productId")),
                        resultSet.getInt("amount"), resultSet.getInt("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public Optional<OrderItem> findById(OrderItemId id) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM orderitems WHERE orderid = ? AND productid = ?");
            statement.setLong(1, id.getOrderId());
            statement.setLong(2, id.getProductId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new OrderItem(new OrderItemId(resultSet.getLong("orderId"), resultSet.getLong("productId")),
                        resultSet.getInt("amount"), resultSet.getInt("price")));
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
    public List<OrderItem> findAll() {
        return List.of();
    }

    @Override
    public OrderItem save(OrderItem entity) {
        Connection connection = pool.retrieve();
        try{
            OrderItem existing = findById(entity.getId()).orElse(null);
            PreparedStatement statement;
            if(existing != null){
                statement = connection.prepareStatement("UPDATE orderItems SET amount = ?, price = ? WHERE orderid = ? AND productid = ?");
                statement.setInt(1, entity.getAmount());
                statement.setLong(2, entity.getPrice());
                statement.setLong(3, entity.getId().getOrderId());
                statement.setLong(4, entity.getId().getProductId());
            } else {
                statement = connection.prepareStatement("INSERT INTO orderItems (orderid, productid, amount, price) VALUES (?, ?, ?, ?)");
                statement.setLong(1, entity.getId().getOrderId());
                statement.setLong(2, entity.getId().getProductId());
                statement.setInt(3, entity.getAmount());
                statement.setLong(4, entity.getPrice());
            }
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return findById(entity.getId()).orElse(null);
        } catch (SQLException e){
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }

        return null;
    }

    @Override
    public void delete(OrderItemId id) {

    }
}
