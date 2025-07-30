package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("/cart/items")
    public String showCart(HttpSession session, Model model) {
        List<CartItemDto> items = cartService.getCartItems(session);
        model.addAttribute("items", items);
        model.addAttribute("total", cartService.getCartTotal(session));
        model.addAttribute("empty", items.isEmpty());
        return "cart";
    }

    @PostMapping("/cart/items/{id}")
    public String handleCartAction(@PathVariable Long id, @RequestParam String action, HttpSession session) {

        if ("plus".equals(action)) {
            cartService.addItemToCart(id, session);
        } else if ("minus".equals(action)) {
            cartService.removeItemFromCart(id, session);
        } else if ("delete".equals(action)) {
            Map<Long, Integer> cart = cartService.getCart(session);
            cart.remove(id);
        }
        return "redirect:/cart/items";
    }

    @PostMapping("/buy")
    public String createOrder(HttpSession session) {
        Map<Long, Integer> cart = cartService.getCart(session);
        User user = userService.getCurrentUser();
        Order order = orderService.createOrderFromCart(cart, user);

        session.removeAttribute("cart");

        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }
}
