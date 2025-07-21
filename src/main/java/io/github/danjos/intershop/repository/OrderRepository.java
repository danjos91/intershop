package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    // List<Order> findByItems_NameContaining(String itemName);
}