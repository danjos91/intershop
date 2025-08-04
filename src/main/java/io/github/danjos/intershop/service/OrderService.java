package io.github.danjos.intershop.service;

import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.*;
import io.github.danjos.intershop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemService itemService;

    public Mono<Order> createOrderFromCart(Map<Long, Integer> cartItems, User user) {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PROCESSING");

        return Flux.fromStream(cartItems.entrySet().stream())
                .flatMap(entry -> 
                    itemService.getItemById(entry.getKey())
                        .map(item -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setItemId(item.getId());
                            orderItem.setQuantity(entry.getValue());
                            orderItem.setPrice(item.getPrice());
                            orderItem.setOrder(order);
                            orderItem.setItem(item);
                            return orderItem;
                        })
                )
                .collectList()
                .flatMap(orderItems -> {
                    order.setItems(orderItems);
                    return orderRepository.save(order);
                });
    }

    public Flux<Order> getUserOrders(User user) {
        return orderRepository.findByUserId(user.getId());
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Order with id " + id + " not found")));
    }
}
