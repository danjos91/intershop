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

    /**
     * Синхронизированный метод для получения корзины из сессии.
     * Предотвращает гонку данных между потоками.
     */
    public synchronized Map<Long, Integer> getCart(HttpSession session) {
        return getCartInternal(session);
    }

    /**
     * Приватный метод для внутреннего использования в сервисе.
     * Не синхронизирован, так как вызывается только из синхронизированных методов.
     */
    private Map<Long, Integer> getCartInternal(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public List<CartItemDto> getCartItems(HttpSession session) {
        Map<Long, Integer> cart = getCartInternal(session);
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
