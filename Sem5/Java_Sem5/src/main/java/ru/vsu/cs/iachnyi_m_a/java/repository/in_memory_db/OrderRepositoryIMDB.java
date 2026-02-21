package ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db;

import ru.vsu.cs.iachnyi_m_a.java.entity.order.Order;
import ru.vsu.cs.iachnyi_m_a.java.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

public class OrderRepositoryIMDB implements OrderRepository {

    private InMemoryDatabase database;

    public OrderRepositoryIMDB(InMemoryDatabase database) {
        this.database = database;
    }

    public List<Order> findAllByUserId(Long userId) {
        return database.getAllOrders().stream().filter(o -> o.getUserId() == userId).toList();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return database.getAllOrders().stream().filter(o -> o.getId() == id).findFirst();
    }

    @Override
    public List<Order> findAll() {
        return database.getAllOrders();
    }

    @Override
    public Order save(Order entity) {
        return database.insertOrder(entity);
    }

    @Override
    public void delete(Long id) {

    }
}
