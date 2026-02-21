package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.Order;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderStatus;
import ru.vsu.cs.iachnyi_m_a.java.repository.OrderRepository;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepositorySQL implements OrderRepository {

    private DatabaseConnectionPool pool;

    public OrderRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<Order> findById(Long id) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Order(resultSet.getLong("id"), resultSet.getLong("userId"),
                        resultSet.getDate("date"), resultSet.getLong("pickupPointId"),
                        OrderStatus.valueOf(resultSet.getString("orderStatus")), new ArrayList<>()));
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
    public List<Order> findAll() {
        List<Order> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new Order(resultSet.getLong("id"), resultSet.getLong("userId"),
                        resultSet.getDate("date"), resultSet.getLong("pickupPointId"),
                        OrderStatus.valueOf(resultSet.getString("orderStatus")), new ArrayList<>()));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public Order save(Order entity) {
        Connection connection = pool.retrieve();
        try{
            Order existing = findById(entity.getId()).orElse(null);
            PreparedStatement statement;
            if(existing != null){
                statement = connection.prepareStatement("UPDATE orders SET userid = ?, date = ?, pickupPointId = ?, orderStatus = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, entity.getUserId());
                statement.setDate(2, new Date(entity.getDate().getTime()));
                statement.setLong(3, entity.getPickupPointId());
                statement.setString(4, entity.getStatus().toString());
                statement.setLong(5, entity.getId());
            } else {
                statement = connection.prepareStatement("INSERT INTO orders (userid, date, pickuppointid, orderstatus) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, entity.getUserId());
                statement.setDate(2, new Date(entity.getDate().getTime()));
                statement.setLong(3, entity.getPickupPointId());
                statement.setString(4, entity.getStatus().toString());
            }
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return findById(resultSet.getLong(1)).orElse(null);
        } catch (SQLException e){
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }

        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
