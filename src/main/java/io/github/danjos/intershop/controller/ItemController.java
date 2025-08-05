package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping("/items/{id}")
    public Mono<Rendering> showItem(@PathVariable Long id, HttpSession session) {
        return Mono.zip(
                itemService.getItemById(id),
                Mono.just(cartService.getCart(session))
            )
            .map(tuple -> {
                var item = tuple.getT1();
                Map<Long, Integer> cart = tuple.getT2();
                int count = cart.getOrDefault(id, 0);
                
                CartItemDto itemWithCount = new CartItemDto(item, count);
                
                return Rendering.view("item")
                        .modelAttribute("item", itemWithCount)
                        .build();
            });
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> handleItemAction(
            @PathVariable Long id,
            @RequestParam String action,
            HttpSession session) {

        Mono<Void> cartOperation = Mono.empty();
        
        if ("plus".equals(action)) {
            cartOperation = cartService.addItemToCartReactive(id, session);
        } else if ("minus".equals(action)) {
            cartOperation = cartService.removeItemFromCartReactive(id, session);
        }
        
        return cartOperation
            .then(Mono.just(Rendering.redirectTo("/items/" + id).build()));
    }
}
