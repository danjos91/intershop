package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public void addItemToCart(Long itemId, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
        session.getAttributes().put("cart", cart);
    }

    public void removeItemFromCart(Long itemId, WebSession session) {
        Map<Long, Integer> cart = getCart(session);
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
            Map<Long, Integer> cart = getCart(session);
            cart.remove(itemId);
            session.getAttributes().put("cart", cart);
        });
    }

    public Map<Long, Integer> getCart(WebSession session) {
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
        Map<Long, Integer> cart = getCart(session);

        Set<Long> ids =  cart.isEmpty() ? new HashSet<>() : cart.keySet();
        
        return itemService.getItemByIds(ids)
                .map(item -> new CartItemDto(item, cart.getOrDefault(item.getId(), 0))).collectList();
    }

    public Mono<Double> getCartTotalReactive(WebSession session) {
        return getCartItemsReactive(session)
                .map(cartItems -> cartItems.stream()
                        .mapToDouble(item -> item.getPrice() * item.getCount())
                        .sum());
    }
}
