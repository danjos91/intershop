package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public Mono<ResponseEntity<List<Order>>> getOrders() {
        return userService.getCurrentUser()
            .flatMap(user -> 
                orderService.getUserOrders(user)
                    .collectList()
                    .map(ResponseEntity::ok)
            )
            .onErrorReturn(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getOrder(
            @PathVariable Long id, 
            @RequestParam(required = false) boolean newOrder) {
        return orderService.getOrderById(id)
            .map(order -> {
                Map<String, Object> response = Map.of(
                    "order", order,
                    "newOrder", newOrder
                );
                return ResponseEntity.ok(response);
            })
            .onErrorReturn(ResponseEntity.notFound().build());
    }
}