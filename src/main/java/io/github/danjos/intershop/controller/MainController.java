package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;

    @GetMapping("/main/items")
    public String showMainPage(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(required = false, defaultValue = "1") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            Model model) {

        List<Item> items = itemService.searchItems(search, sort);
        // Implementar paginación aquí si es necesario

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        return "main";
    }
}
