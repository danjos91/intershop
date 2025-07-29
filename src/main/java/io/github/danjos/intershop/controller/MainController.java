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
import org.springframework.ui.Model;
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
    public String showMainPage(
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            Model model,
            HttpSession session) {

        Page<Item> mainPage = itemService.searchItems(search, pageNumber, pageSize, sort);
        Paging paging = new Paging(pageNumber, pageSize, mainPage.hasNext(), mainPage.hasPrevious());

        // Get cart information to show count for each item
        Map<Long, Integer> cart = cartService.getCart(session);
        
        // Convert items to CartItemDto with cart count
        List<CartItemDto> itemsWithCount = mainPage.getContent().stream()
                .map(item -> new CartItemDto(item, cart.getOrDefault(item.getId(), 0)))
                .collect(Collectors.toList());

        model.addAttribute("items", itemsWithCount);
        model.addAttribute("search", search);
        model.addAttribute("paging", paging);
        return "main";
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
        return "redirect:/main/items";
    }
}
