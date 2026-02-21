package ru.vsu.cs.iachnyi_m_a.java.service;

import ru.vsu.cs.iachnyi_m_a.java.entity.order.Order;
import ru.vsu.cs.iachnyi_m_a.java.entity.order.OrderItem;
import ru.vsu.cs.iachnyi_m_a.java.repository.OrderItemRepository;
import ru.vsu.cs.iachnyi_m_a.java.repository.OrderRepository;

import java.util.List;

public class OrderService {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order addOrder(Order order) {
        Order res = orderRepository.save(order);
        for(OrderItem orderItem: order.getItems()) {
            orderItem.getId().setOrderId(res.getId());
            orderItemRepository.save(orderItem);
        }
        res.setItems(orderItemRepository.findAllByOrderId(res.getId()));
        return res;
    }

    public Order getOrderById(long id) {
        Order res = orderRepository.findById(id).orElse(null);
        if(res != null) {
            res.setItems(orderItemRepository.findAllByOrderId(id));
        }
        return res;
    }

    public List<Order> getAllOrdersByUserId(long userId) {
        List<Order> res = orderRepository.findAll().stream().filter(o -> o.getUserId() == userId).toList();
        for(Order order: res) {
            order.setItems(orderItemRepository.findAllByOrderId(order.getId()));
        }
        return res;
    }
}
