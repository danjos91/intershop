package io.github.danjos.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

@Table("order_items")
@Data
public class OrderItem {
    @Id
    private Long id;

    private Long orderId;
    private Long itemId;

    private int quantity;
    private double price;

    private transient Order order;
    private transient Item item;
}
