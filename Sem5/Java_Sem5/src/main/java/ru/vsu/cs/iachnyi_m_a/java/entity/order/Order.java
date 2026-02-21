package ru.vsu.cs.iachnyi_m_a.java.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class Order {
    private long id;
    private long userId;
    private Date date;
    private long pickupPointId;
    private OrderStatus status;
    private List<OrderItem> items;

    public Order(Order order1) {
        this.id = order1.getId();
        this.userId = order1.getUserId();
        this.date = new Date(order1.getDate().getTime());
        this.pickupPointId = order1.getPickupPointId();
        this.status = order1.getStatus();
        this.items = List.copyOf(order1.getItems());
    }
}
