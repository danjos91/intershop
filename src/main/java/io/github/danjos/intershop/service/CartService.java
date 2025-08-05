package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public synchronized void addItemToCart(Long itemId, HttpSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
        session.setAttribute("cart", cart);
    }

    public synchronized void removeItemFromCart(Long itemId, HttpSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        if (cart.containsKey(itemId)) {
            if (cart.get(itemId) > 1) {
                cart.put(itemId, cart.get(itemId) - 1);
            } else {
                cart.remove(itemId);
            }
        }
        session.setAttribute("cart", cart);
    }

    public Mono<Void> addItemToCartReactive(Long itemId, HttpSession session) {
        return Mono.fromRunnable(() -> addItemToCart(itemId, session));
    }

    public Mono<Void> removeItemFromCartReactive(Long itemId, HttpSession session) {
        return Mono.fromRunnable(() -> removeItemFromCart(itemId, session));
    }

    public Mono<Void> deleteItemFromCartReactive(Long itemId, HttpSession session) {
        return Mono.fromRunnable(() -> {
            Map<Long, Integer> cart = getCartInternal(session);
            cart.remove(itemId);
            session.setAttribute("cart", cart);
        });
    }

    public synchronized Map<Long, Integer> getCart(HttpSession session) {
        return getCartInternal(session);
    }

    private Map<Long, Integer> getCartInternal(HttpSession session) {
        Object cartAttribute = session.getAttribute("cart");
        Map<Long, Integer> cart;
        if (cartAttribute instanceof Map) {
            cart = (Map<Long, Integer>) cartAttribute;
        } else {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public List<CartItemDto> getCartItems(HttpSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        return cart.entrySet().stream()
                .map(entry -> {
                    Item item = itemService.getItemById(entry.getKey()).block();
                    return new CartItemDto(item, entry.getValue());
                })
                .collect(Collectors.toList());
    }

    public double getCartTotal(HttpSession session) {
        return getCartItems(session).stream()
                .mapToDouble(item -> item.getPrice() * item.getCount())
                .sum();
    }

    public Flux<CartItemDto> getCartItemsFlux(HttpSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        return Flux.fromStream(cart.entrySet().stream())
                .flatMap(entry -> 
                    itemService.getItemById(entry.getKey())
                        .map(item -> new CartItemDto(item, entry.getValue()))
                );
    }

    public Mono<List<CartItemDto>> getCartItemsReactive(HttpSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
        
        return Flux.fromStream(cart.entrySet().stream())
                .flatMap(entry -> 
                    itemService.getItemById(entry.getKey())
                        .map(item -> new CartItemDto(item, entry.getValue()))
                )
                .collectList();
    }

    public Mono<Double> getCartTotalReactive(HttpSession session) {
        return getCartItemsReactive(session)
                .map(cartItems -> cartItems.stream()
                        .mapToDouble(item -> item.getPrice() * item.getCount())
                        .sum());
    }
}
