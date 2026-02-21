package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositorySQL implements UserRepository {

    private DatabaseConnectionPool pool;

    public UserRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("password")));
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
    public Optional<User> findById(Long id) {
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("password")));
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
    public List<User> findAll() {
        List<User> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new User(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getString("password")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public User save(User entity) {
        Connection connection = pool.retrieve();
        try{
            User existing = findById(entity.getId()).orElse(null);
            PreparedStatement statement;
            if(existing != null){
                statement = connection.prepareStatement("UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, entity.getName());
                statement.setString(2, entity.getEmail());
                statement.setString(3, entity.getPassword());
                statement.setLong(4, entity.getId());
            } else {
                statement = connection.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, entity.getName());
                statement.setString(2, entity.getEmail());
                statement.setString(3, entity.getPassword());
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
