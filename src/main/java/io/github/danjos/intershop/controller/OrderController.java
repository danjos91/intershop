package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/orders")
    public String showOrders(Model model) {
        User user = userService.getCurrentUserBlocking();
        model.addAttribute("orders", orderService.getUserOrders(user).collectList().block());
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String showOrder(@PathVariable Long id, @RequestParam(required = false) boolean newOrder, Model model) {

        Order order = orderService.getOrderById(id).block();
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }
}