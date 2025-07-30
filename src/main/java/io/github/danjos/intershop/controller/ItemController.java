package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CartService cartService;

    @GetMapping("/items/{id}")
    public String showItem(@PathVariable Long id, Model model, HttpSession session) {
        Map<Long, Integer> cart = cartService.getCart(session);
        int count = cart.getOrDefault(id, 0);
        
        CartItemDto itemWithCount = new CartItemDto(itemService.getItemById(id), count);
        model.addAttribute("item", itemWithCount);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String handleItemAction(
            @PathVariable Long id,
            @RequestParam String action,
            HttpSession session) {

        if ("plus".equals(action)) {
            cartService.addItemToCart(id, session);
        } else if ("minus".equals(action)) {
            cartService.removeItemFromCart(id, session);
        }
        return "redirect:/items/" + id;
    }
}
