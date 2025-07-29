package io.github.danjos.intershop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    private LocalDateTime orderDate;
    private String status;

    public double getTotalSum() {
        return items.stream().mapToDouble(oi -> oi.getPrice() * oi.getQuantity()).sum();
    }

    public Long getId() {
        return id;
    }
}
