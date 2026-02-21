package ru.vsu.cs.iachnyi_m_a.java.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemId {
    private long orderId;
    private long productId;

    public OrderItemId(OrderItemId orderItemId1){
        this.orderId = orderItemId1.orderId;
        this.productId = orderItemId1.productId;
    }
}
