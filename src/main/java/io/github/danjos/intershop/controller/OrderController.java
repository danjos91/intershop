package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/orders")
    public Mono<Rendering> showOrders() {
        return userService.getCurrentUser()
            .flatMap(user -> 
                orderService.getUserOrders(user)
                    .collectList()
                    .map(orders -> 
                        Rendering.view("orders")
                            .modelAttribute("orders", orders)
                            .build()
                    )
            );
    }

    @GetMapping("/orders/{id}")
    public Mono<Rendering> showOrder(@PathVariable Long id, @RequestParam(required = false) boolean newOrder) {
        return orderService.getOrderById(id)
            .map(order -> 
                Rendering.view("order")
                    .modelAttribute("order", order)
                    .modelAttribute("newOrder", newOrder)
                    .build()
            );
    }
}