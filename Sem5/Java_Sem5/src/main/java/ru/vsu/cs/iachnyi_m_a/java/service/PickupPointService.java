package ru.vsu.cs.iachnyi_m_a.java.service;

import ru.vsu.cs.iachnyi_m_a.java.entity.PickupPoint;
import ru.vsu.cs.iachnyi_m_a.java.repository.PickupPointRepository;

import java.util.List;

public class PickupPointService {

    private PickupPointRepository repository;

    public PickupPointService(PickupPointRepository repository) {
        this.repository = repository;
    }

    public List<PickupPoint> getAllPickupPoints() {
        return repository.findAll();
    }

    public PickupPoint getPickupPointById(long id) {
        return repository.findById(id).orElse(null);
    }
}
