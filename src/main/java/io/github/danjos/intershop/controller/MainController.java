package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import io.github.danjos.intershop.util.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/main", "/"})
public class MainController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping({"/", "/items"})
    public Mono<Rendering> showMainPage(
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            HttpSession session) {

        return Mono.zip(
                itemService.searchItems(search, pageNumber, pageSize, sort),
                Mono.just(cartService.getCart(session))
            )
            .map(tuple -> {
                Page<Item> mainPage = tuple.getT1();
                Map<Long, Integer> cart = tuple.getT2();
                
                Paging paging = new Paging(pageNumber, pageSize, mainPage.hasNext(), mainPage.hasPrevious());
                
                List<CartItemDto> itemsWithCount = mainPage.getContent().stream()
                        .map(item -> new CartItemDto(item, cart.getOrDefault(item.getId(), 0)))
                        .collect(Collectors.toList());

                return Rendering.view("main")
                        .modelAttribute("items", itemsWithCount)
                        .modelAttribute("search", search)
                        .modelAttribute("paging", paging)
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
            .then(Mono.just(Rendering.redirectTo("/main/items").build()));
    }
}
