package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CartItemDto>> getItem(@PathVariable Long id, WebSession session) {
        return Mono.zip(
                itemService.getItemById(id),
                Mono.just(cartService.getCart(session))
            )
            .map(tuple -> {
                var item = tuple.getT1();
                Map<Long, Integer> cart = tuple.getT2();
                int count = cart.getOrDefault(id, 0);
                
                CartItemDto itemWithCount = new CartItemDto(item, count);
                
                return ResponseEntity.ok(itemWithCount);
            })
            .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public Mono<ResponseEntity<Map<String, String>>> handleItemAction(
            @PathVariable Long id,
            @RequestParam String action,
            WebSession session) {

        Mono<Void> cartOperation = Mono.empty();
        
        if ("plus".equals(action)) {
            cartOperation = cartService.addItemToCartReactive(id, session);
        } else if ("minus".equals(action)) {
            cartOperation = cartService.removeItemFromCartReactive(id, session);
        }
        
        return cartOperation
            .then(Mono.just(ResponseEntity.ok(Map.of("message", "Item " + action + " successful"))))
            .onErrorReturn(ResponseEntity.badRequest().body(Map.of("error", "Failed to " + action + " item")));
    }
}
