package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemService itemService;

    public synchronized void addItemToCart(Long itemId, WebSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
        session.getAttributes().put("cart", cart);
    }

    public synchronized void removeItemFromCart(Long itemId, WebSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        if (cart.containsKey(itemId)) {
            if (cart.get(itemId) > 1) {
                cart.put(itemId, cart.get(itemId) - 1);
            } else {
                cart.remove(itemId);
            }
        }
        session.getAttributes().put("cart", cart);
    }

    public Mono<Void> addItemToCartReactive(Long itemId, WebSession session) {
        return Mono.fromRunnable(() -> addItemToCart(itemId, session));
    }

    public Mono<Void> removeItemFromCartReactive(Long itemId, WebSession session) {
        return Mono.fromRunnable(() -> removeItemFromCart(itemId, session));
    }

    public Mono<Void> deleteItemFromCartReactive(Long itemId, WebSession session) {
        return Mono.fromRunnable(() -> {
            Map<Long, Integer> cart = getCartInternal(session);
            cart.remove(itemId);
            session.getAttributes().put("cart", cart);
        });
    }

    public synchronized Map<Long, Integer> getCart(WebSession session) {
        return getCartInternal(session);
    }

    private Map<Long, Integer> getCartInternal(WebSession session) {
        Object cartAttribute = session.getAttributes().get("cart");
        Map<Long, Integer> cart;
        if (cartAttribute instanceof Map) {
            cart = (Map<Long, Integer>) cartAttribute;
        } else {
            cart = new HashMap<>();
            session.getAttributes().put("cart", cart);
        }
        return cart;
    }

    public List<CartItemDto> getCartItems(WebSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        return cart.entrySet().stream()
                .map(entry -> {
                    Item item = itemService.getItemById(entry.getKey()).block();
                    return new CartItemDto(item, entry.getValue());
                })
                .collect(Collectors.toList());
    }

    public double getCartTotal(WebSession session) {
        return getCartItems(session).stream()
                .mapToDouble(item -> item.getPrice() * item.getCount())
                .sum();
    }

    public Flux<CartItemDto> getCartItemsFlux(WebSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        return Flux.fromStream(cart.entrySet().stream())
                .flatMap(entry -> 
                    itemService.getItemById(entry.getKey())
                        .map(item -> new CartItemDto(item, entry.getValue()))
                );
    }

    public Mono<List<CartItemDto>> getCartItemsReactive(WebSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        
        return Flux.fromStream(cart.entrySet().stream())
                .flatMap(entry -> 
                    itemService.getItemById(entry.getKey())
                        .map(item -> new CartItemDto(item, entry.getValue()))
                )
                .collectList();
    }

    public Mono<Double> getCartTotalReactive(WebSession session) {
        return getCartItemsReactive(session)
                .map(cartItems -> cartItems.stream()
                        .mapToDouble(item -> item.getPrice() * item.getCount())
                        .sum());
    }
}
