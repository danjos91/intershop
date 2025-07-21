package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemService itemService;

    public void addItemToCart(Long itemId, HttpSession session) {
        // Implementación del carrito en sesión
        Map<Long, Integer> cart = getCart(session);
        cart.put(itemId, cart.getOrDefault(itemId, 0) + 1);
    }

    public void removeItemFromCart(Long itemId, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        if (cart.containsKey(itemId)) {
            if (cart.get(itemId) > 1) {
                cart.put(itemId, cart.get(itemId) - 1);
            } else {
                cart.remove(itemId);
            }
        }
    }

    public Map<Long, Integer> getCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public List<CartItemDto> getCartItems(HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        return cart.entrySet().stream()
                .map(entry -> {
                    Item item = itemService.getItemById(entry.getKey());
                    return new CartItemDto(item, entry.getValue());
                })
                .collect(Collectors.toList());
    }

    public double getCartTotal(HttpSession session) {
        return getCartItems(session).stream()
                .mapToDouble(item -> item.getPrice() * item.getCount())
                .sum();
    }
}
