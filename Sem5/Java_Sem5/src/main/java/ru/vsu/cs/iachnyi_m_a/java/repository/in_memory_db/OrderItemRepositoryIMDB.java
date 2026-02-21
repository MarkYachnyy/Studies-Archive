package ru.vsu.cs.iachnyi_m_a.java.repository.in_memory_db;

import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItemId;
import ru.vsu.cs.iachnyi_m_a.java.repository.OrderItemRepository;

import java.util.List;
import java.util.Optional;

public class OrderItemRepositoryIMDB implements OrderItemRepository {

    private InMemoryDatabase database;

    public OrderItemRepositoryIMDB(InMemoryDatabase database) {
        this.database = database;
    }

    @Override
    public Optional<OrderItem> findById(OrderItemId id) {
        return Optional.empty();
    }

    @Override
    public List<OrderItem> findAll() {
        return List.of();
    }

    @Override
    public OrderItem save(OrderItem entity) {
        return database.insertOrderItem(entity);
    }

    @Override
    public void delete(OrderItemId id) {

    }

    @Override
    public List<OrderItem> findAllByOrderId(long orderId) {
        return database.getAllOrderItems().stream().filter(oi -> oi.getId().getOrderId() == orderId).toList();
    }
}
