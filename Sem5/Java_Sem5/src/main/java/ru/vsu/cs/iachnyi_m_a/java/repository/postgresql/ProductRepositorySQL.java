package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.Product;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositorySQL implements ProductRepository {

    private DatabaseConnectionPool pool;

    public ProductRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public List<Product> findAllBySellerId(Long sellerId) {
        List<Product> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE sellerId = ?");
            statement.setLong(1, sellerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new Product(resultSet.getLong("id"), resultSet.getLong("sellerId"), resultSet.getString("name"),
                        resultSet.getInt("price"), resultSet.getInt("stockQuantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public Optional<Product> findById(Long id) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM products WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Product(resultSet.getLong("id"), resultSet.getLong("sellerId"), resultSet.getString("name"),
                        resultSet.getInt("price"), resultSet.getInt("stockQuantity")));
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
    public List<Product> findAll() {
        List<Product> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM products");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new Product(resultSet.getLong("id"), resultSet.getLong("sellerId"), resultSet.getString("name"),
                        resultSet.getInt("price"), resultSet.getInt("stockQuantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public Product save(Product entity) {
        Product res = null;
        Connection connection = pool.retrieve();
        try{
            Product existing = findById(entity.getId()).orElse(null);
            PreparedStatement statement;
            if(existing != null){
                statement = connection.prepareStatement("UPDATE products SET sellerid = ?, name = ?, price = ?, stockQuantity = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, existing.getSellerId());
                statement.setString(2, entity.getName());
                statement.setInt(3, entity.getPrice());
                statement.setInt(4, entity.getStockQuantity());
                statement.setLong(5, entity.getId());
            } else {
                statement = connection.prepareStatement("INSERT INTO products (sellerid, name, price, stockQuantity) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, entity.getSellerId());
                statement.setString(2, entity.getName());
                statement.setInt(3, entity.getPrice());
                statement.setInt(4, entity.getStockQuantity());
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
