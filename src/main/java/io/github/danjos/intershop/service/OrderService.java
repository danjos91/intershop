package io.github.danjos.intershop.service;

import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.*;
import io.github.danjos.intershop.repository.OrderRepository;
import io.github.danjos.intershop.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;

    public Mono<Order> createOrderFromCart(Map<Long, Integer> cartItems, User user) {
        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PROCESSING");

        return orderRepository.save(order)
                .flatMap(savedOrder -> 
                    Flux.fromStream(cartItems.entrySet().stream())
                        .flatMap(entry -> 
                            itemService.getItemById(entry.getKey())
                                .map(item -> {
                                    OrderItem orderItem = new OrderItem();
                                    orderItem.setOrderId(savedOrder.getId());
                                    orderItem.setItemId(item.getId());
                                    orderItem.setQuantity(entry.getValue());
                                    orderItem.setPrice(item.getPrice());
                                    orderItem.setOrder(savedOrder);
                                    orderItem.setItem(item);
                                    return orderItem;
                                })
                        )
                        .collectList()
                        .flatMap(orderItems -> 
                            Flux.fromIterable(orderItems)
                                .flatMap(orderItemRepository::save)
                                .collectList()
                                .map(savedOrderItems -> {
                                    savedOrder.setItems(savedOrderItems);
                                    return savedOrder;
                                })
                        )
                );
    }

    public Flux<Order> getUserOrders(User user) {
        return orderRepository.findByUserId(user.getId())
                .flatMap(order -> 
                    orderItemRepository.findByOrderId(order.getId())
                        .flatMap(orderItem -> 
                            itemService.getItemById(orderItem.getItemId())
                                .map(item -> {
                                    orderItem.setItem(item);
                                    orderItem.setOrder(order);
                                    return orderItem;
                                })
                        )
                        .collectList()
                        .map(orderItems -> {
                            order.setItems(orderItems);
                            return order;
                        })
                );
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Order with id " + id + " not found")))
                .flatMap(order -> 
                    orderItemRepository.findByOrderId(order.getId())
                        .flatMap(orderItem -> 
                            itemService.getItemById(orderItem.getItemId())
                                .map(item -> {
                                    orderItem.setItem(item);
                                    orderItem.setOrder(order);
                                    return orderItem;
                                })
                        )
                        .collectList()
                        .map(orderItems -> {
                            order.setItems(orderItems);
                            return order;
                        })
                );
    }
}
