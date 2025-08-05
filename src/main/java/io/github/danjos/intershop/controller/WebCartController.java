package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebCartController {
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/cart/items")
    public Mono<Rendering> showCart(WebSession session) {
        return Mono.zip(
                cartService.getCartItemsReactive(session),
                cartService.getCartTotalReactive(session),
                userService.getCurrentUser()
            )
            .map(tuple -> {
                List<CartItemDto> items = tuple.getT1();
                Double total = tuple.getT2();
                User user = tuple.getT3();
                
                return Rendering.view("cart")
                        .modelAttribute("items", items)
                        .modelAttribute("total", total)
                        .modelAttribute("empty", items.isEmpty())
                        .modelAttribute("user", user)
                        .build();
            });
    }

    @PostMapping("/cart/items/{id}")
    public Mono<Rendering> handleCartAction(
            @PathVariable Long id, 
            @RequestParam String action, 
            WebSession session) {
        
        Mono<Void> cartOperation = Mono.empty();
        
        if ("plus".equals(action)) {
            cartOperation = cartService.addItemToCartReactive(id, session);
        } else if ("minus".equals(action)) {
            cartOperation = cartService.removeItemFromCartReactive(id, session);
        } else if ("delete".equals(action)) {
            cartOperation = cartService.deleteItemFromCartReactive(id, session);
        }
        
        return cartOperation
            .then(Mono.just(Rendering.redirectTo("/cart/items").build()));
    }

    @PostMapping("/buy")
    public Mono<Rendering> createOrder(WebSession session) {
        return Mono.zip(
                Mono.just(cartService.getCart(session)),
                userService.getCurrentUser()
            )
            .flatMap(tuple -> {
                Map<Long, Integer> cart = tuple.getT1();
                User user = tuple.getT2();
                
                return orderService.createOrderFromCart(cart, user)
                    .map(order -> {
                        session.getAttributes().remove("cart");
                        return Rendering.redirectTo("/orders/" + order.getId() + "?newOrder=true").build();
                    });
            });
    }
} 