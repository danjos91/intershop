package io.github.danjos.intershop.controller;


import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.ItemService;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.util.Paging;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({"/items", "/"})
public class ItemController {

    private final ItemService itemService;
    // private final OrderService orderService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String getItems(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            Model model) {

        Page<Item> itemsPage = itemService.getItems(search, pageNumber, pageSize);
//        for(Item item : itemsPage.getContent()) {
//            List<Comment> comments = commentService.getCommentsByPostId(pm.getId());
//            pm.setComments(comments);
//        }
        Paging paging = new Paging(pageNumber, pageSize, itemsPage.hasNext(), itemsPage.hasPrevious());
        model.addAttribute("items", itemsPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("paging", paging);

        return "items";
    }

    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id);
        // List<Comment> comments = commentService.getCommentsByPostId(id);
        //post.setComments(comments);
        model.addAttribute("item", item);
        return "post";
    }

//    @GetMapping("/add")
//    public String showAddPostForm(Model model) {
//        model.addAttribute("post", new PostModel());
//        return "add-post";
//    }

    @PostMapping
    public String addItem(
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam String tags,
            @RequestParam (required = false) MultipartFile image) throws IOException {

        itemService.createItem(title, text, image, tags);
        return "redirect:/posts";
    }

//    @GetMapping("/images/{id}")
//    @ResponseBody
//    public byte[] getImage(@PathVariable Long id) {
//        return postService.getPostImage(id);
//    }

//    @PostMapping("/{id}/like")
//    public String likePost(
//            @PathVariable Long id,
//            @RequestParam boolean like) {
//
//        itemService.likePost(id, like);
//        return "redirect:/posts/" + id;
//    }

//    @GetMapping("/{id}/edit")
//    public String showEditPostForm(@PathVariable Long id, Model model) {
//        PostModel post = postService.getPostById(id);
//        model.addAttribute("post", post);
//        return "add-post";
//    }

//    @PostMapping("/{id}")
//    public String updatePost(
//            @PathVariable Long id,
//            @RequestParam String title,
//            @RequestParam String text,
//            @RequestParam(required = false) MultipartFile image,
//            @RequestParam(required = false, defaultValue = "") String tags) throws IOException {
//
//        postService.updatePost(id, title, text, image, tags);
//        return "redirect:/posts/" + id;
//    }

//    @PostMapping("/{id}/comments")
//    public String addComment(
//            @PathVariable Long id,
//            @RequestParam String text) {
//
//        commentService.addComment(id, text);
//        return "redirect:/posts/" + id;
//    }

//    @PostMapping("/{id}/comments/{commentId}")
//    public String editComment(
//            @PathVariable Long id,
//            @PathVariable Long commentId,
//            @RequestParam String text) {
//
//        commentService.editComment(commentId, text);
//        return "redirect:/posts/" + id;
//    }

//    @PostMapping("/{id}/comments/{commentId}/delete")
//    public String deleteComment(
//            @PathVariable Long id,
//            @PathVariable Long commentId) {
//
//        commentService.deleteComment(commentId);
//        return "redirect:/posts/" + id;
//    }


    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id) {

        itemService.deleteItem(id);
        return "redirect:/posts";
    }
}