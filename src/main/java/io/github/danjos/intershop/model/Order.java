package io.github.danjos.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("orders")
@Data
public class Order {
    @Id
    private Long id;

    private Long userId;

    private LocalDateTime orderDate;
    private String status;

    private transient List<OrderItem> items = new ArrayList<>();

    public double getTotalSum() {
        return items.stream().mapToDouble(oi -> oi.getPrice() * oi.getQuantity()).sum();
    }
}
