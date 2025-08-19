package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {

    Flux<OrderItem> findByOrderId(Long orderId);
    
    Flux<OrderItem> findByItemId(Long itemId);
    
    Flux<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);
} 