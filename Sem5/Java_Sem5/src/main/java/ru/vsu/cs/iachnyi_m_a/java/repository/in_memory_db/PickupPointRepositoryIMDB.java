package ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db;

import ru.vsu.cs.iachnyi_m_a.java.entity.PickupPoint;
import ru.vsu.cs.iachnyi_m_a.java.repository.PickupPointRepository;

import java.util.List;
import java.util.Optional;

public class PickupPointRepositoryIMDB implements PickupPointRepository {

    InMemoryDatabase database = InMemoryDatabase.getInstance();

    public PickupPointRepositoryIMDB(InMemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Optional<PickupPoint> findById(Long id) {
        return database.getAllPickupPoints().stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<PickupPoint> findAll() {
        return database.getAllPickupPoints();
    }

    @Override
    public PickupPoint save(PickupPoint entity) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
