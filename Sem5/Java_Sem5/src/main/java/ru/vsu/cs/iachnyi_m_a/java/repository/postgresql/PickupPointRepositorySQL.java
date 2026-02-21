package ru.vsu.cs.iachnyi_m_a.java.repository.postgresql;

import ru.vsu.cs.iachnyi_m_a.java.database.DatabaseConnectionPool;
import ru.vsu.cs.iachnyi_m_a.java.entity.PickupPoint;
import ru.vsu.cs.iachnyi_m_a.java.entity.Seller;
import ru.vsu.cs.iachnyi_m_a.java.entity.User;
import ru.vsu.cs.iachnyi_m_a.java.repository.PickupPointRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PickupPointRepositorySQL implements PickupPointRepository {

    private DatabaseConnectionPool pool;

    public PickupPointRepositorySQL(DatabaseConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public Optional<PickupPoint> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<PickupPoint> findAll() {
        List<PickupPoint> res = new ArrayList<>();
        Connection connection = pool.retrieve();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pickuppoints");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                res.add(new PickupPoint(resultSet.getLong("id"), resultSet.getString("address")));
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            pool.release(connection);
        }
        return res;
    }

    @Override
    public PickupPoint save(PickupPoint entity) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
