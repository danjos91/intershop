package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.ItemService;
import io.github.danjos.intershop.util.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/main", "/"})
public class MainController {
    private final ItemService itemService;

    @GetMapping
    public String showMainPage(
            @RequestParam(required = false, defaultValue = "NO") String sort,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            Model model) {

        //List<Item> items = itemService.searchItems(search, sort);

        Page<Item> mainPage = itemService.searchItems(search, pageNumber, pageSize, sort);
//        for(PostModel pm : postPage.getContent()) {
//            List<Comment> comments = commentService.getCommentsByPostId(pm.getId());
//            pm.setComments(comments);
//        }
        Paging paging = new Paging(pageNumber, pageSize, mainPage.hasNext(), mainPage.hasPrevious());

        model.addAttribute("items", mainPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("paging", paging);
        return "main";
    }
}
