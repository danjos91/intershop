package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import io.github.danjos.intershop.util.Paging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class WebItemController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping("/{id}")
    public Mono<Rendering> showItem(@PathVariable Long id, WebSession session) {
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
                })
                .onErrorResume(e -> {
                    log.error("Error in showItem", e);
                    return Mono.just(Rendering.redirectTo("/error").build());
                });
    }

    @GetMapping("/order/{id}")
    public Mono<Rendering> handleItemAction(
            @PathVariable Long id,
            @RequestParam String action,
            WebSession session) {

        log.info("Handling item action: {} for item: {}", action, id);

        Mono<Void> cartOperation = Mono.empty();

        if ("plus".equals(action)) {
            cartOperation = cartService.addItemToCartReactive(id, session);
        } else if ("minus".equals(action)) {
            cartOperation = cartService.removeItemFromCartReactive(id, session);
        }

        return cartOperation
                .then(Mono.just(Rendering.redirectTo("/items/" + id).build()))
                .onErrorResume(e -> {
                    log.error("Error in handleItemAction", e);
                    return Mono.just(Rendering.redirectTo("/items/" + id).build());
                });
    }
}