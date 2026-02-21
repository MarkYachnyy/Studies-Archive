package ru.vsu.cs.iachnyi_m_a.java.repository;

import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItem;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItemId;

import java.util.List;

public interface OrderItemRepository extends DataRepository<OrderItem, OrderItemId>{
    public List<OrderItem> findAllByOrderId(long orderId);
}
