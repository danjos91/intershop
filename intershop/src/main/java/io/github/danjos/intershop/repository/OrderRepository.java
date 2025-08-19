package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Flux<Order> findByUserId(Long userId);

}