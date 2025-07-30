package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({MainController.class, ItemController.class, CartController.class})
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    private Item laptop;
    private Item smartphone;
    private Page<Item> itemPage;

    @BeforeEach
    void setUp() {
        laptop = new Item();
        laptop.setId(1L);
        laptop.setTitle("Laptop");
        laptop.setDescription("High performance laptop");
        laptop.setPrice(999.99);
        laptop.setStock(10);

        smartphone = new Item();
        smartphone.setId(2L);
        smartphone.setTitle("Smartphone");
        smartphone.setDescription("Latest smartphone");
        smartphone.setPrice(599.99);
        smartphone.setStock(15);

        itemPage = new PageImpl<>(List.of(laptop, smartphone), PageRequest.of(0, 10), 2);
    }

    @Test
    void showMainPage_ShouldReturnMainView() throws Exception {
        when(itemService.searchItems("", 1, 10, "NO"))
                .thenReturn(itemPage);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void showMainPage_WithSearchQuery_ShouldReturnFilteredResults() throws Exception {
        // Given
        when(itemService.searchItems("laptop", 1, 10, "NO"))
                .thenReturn(new PageImpl<>(List.of(laptop), PageRequest.of(0, 10), 1));

        // When & Then http://localhost:8080/?search=lap&action=&sort=NO&pageSize=10
        mockMvc.perform(get("/")
                        .param("search", "laptop"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void showMainPage_WithSorting_ShouldReturnSortedResults() throws Exception {
        // Given
        when(itemService.searchItems("", 1, 10, "ALPHA"))
                .thenReturn(itemPage);

        // When & Then
        mockMvc.perform(get("/")
                        .param("sort", "ALPHA"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void showMainPage_WithPagination_ShouldReturnCorrectPage() throws Exception {
        // Given
        when(itemService.searchItems("", 2, 10, "NO"))
                .thenReturn(itemPage);

        // When & Then
        mockMvc.perform(get("/")
                        .param("pageNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void showItemPage_WithValidId_ShouldReturnItemView() throws Exception {
        // Given
        when(itemService.getItemById(1L)).thenReturn(laptop);

        // When & Then
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    void showItemPage_WithInvalidId_ShouldThrowException() throws Exception {
        // Given
        when(itemService.getItemById(999L))
                .thenThrow(new NotFoundException("Item with id 999 not found"));

        // When & Then
        mockMvc.perform(get("/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addToCart_WithValidItemId_ShouldRedirectToCart() throws Exception {
        // When & Then
        mockMvc.perform(post("/cart/items/1")
                        .param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));
    }

    @Test
    void addToCart_WithInvalidItemId_ShouldHandleError() throws Exception {
        // Given
        when(itemService.getItemById(999L))
                .thenThrow(new NotFoundException("Item with id 999 not found"));

        // When & Then
        mockMvc.perform(post("/items/999/add-to-cart"))
                .andExpect(status().isNotFound());
    }
} 