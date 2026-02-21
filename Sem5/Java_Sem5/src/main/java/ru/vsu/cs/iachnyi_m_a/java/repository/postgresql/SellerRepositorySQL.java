package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.Seller;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.repository.SellerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SellerRepositorySQL implements SellerRepository {

    private DatabaseConnectionPool pool;

    public SellerRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<Seller> findById(Long id) {
        try {
            Connection connection = pool.retrieve();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM sellers WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Seller(resultSet.getLong("id"), resultSet.getString("name")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return Optional.empty();
    }

    @Override
    public List<Seller> findAll() {
        return List.of();
    }

    @Override
    public Seller save(Seller entity) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
