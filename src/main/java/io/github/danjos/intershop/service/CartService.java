package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemService itemService;

    public synchronized void addItemToCart(Long itemId, WebSession session) {
        if (session == null) {
            return;
        }
        Map<Long, Integer> cart = getCartInternal(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
        session.getAttributes().put("cart", cart);
    }

    public synchronized void removeItemFromCart(Long itemId, WebSession session) {
        if (session == null) {
            return;
        }
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
            if (session == null) {
                return;
            }
            Map<Long, Integer> cart = getCartInternal(session);
            cart.remove(itemId);
            session.getAttributes().put("cart", cart);
        });
    }

    public synchronized Map<Long, Integer> getCart(WebSession session) {
        if (session == null) {
            return new HashMap<>();
        }
        return getCartInternal(session);
    }

    private Map<Long, Integer> getCartInternal(WebSession session) {
        if (session == null || session.getAttributes() == null) {
            return new HashMap<>();
        }
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

    public Mono<List<CartItemDto>> getCartItemsReactive(WebSession session) {
        if (session == null) {
            return Mono.just(new ArrayList<>());
        }
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
