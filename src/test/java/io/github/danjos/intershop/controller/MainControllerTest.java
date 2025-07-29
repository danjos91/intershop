package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.ItemService;
import io.github.danjos.intershop.util.Paging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CartService cartService;

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
        // Given
        when(itemService.searchItems(anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(itemPage);

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    void showMainPage_WithSearchQuery_ShouldReturnFilteredResults() throws Exception {
        // Given
        when(itemService.searchItems("laptop", 1, 10, null))
                .thenReturn(new PageImpl<>(List.of(laptop), PageRequest.of(0, 10), 1));

        // When & Then
        mockMvc.perform(get("/")
                        .param("search", "laptop"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"));
    }

    @Test
    void showMainPage_WithSorting_ShouldReturnSortedResults() throws Exception {
        // Given
        when(itemService.searchItems(null, 1, 10, "ALPHA"))
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
        when(itemService.searchItems(null, 2, 10, null))
                .thenReturn(itemPage);

        // When & Then
        mockMvc.perform(get("/")
                        .param("page", "2"))
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
                .andExpect(model().attribute("item", laptop));
    }

    @Test
    void showItemPage_WithInvalidId_ShouldThrowException() throws Exception {
        // Given
        when(itemService.getItemById(999L))
                .thenThrow(new RuntimeException("Item not found"));

        // When & Then
        mockMvc.perform(get("/items/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addToCart_WithValidItemId_ShouldRedirectToCart() throws Exception {
        // When & Then
        mockMvc.perform(post("/items/1/add-to-cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));
    }

    @Test
    void addToCart_WithInvalidItemId_ShouldHandleError() throws Exception {
        // Given
        when(itemService.getItemById(999L))
                .thenThrow(new RuntimeException("Item not found"));

        // When & Then
        mockMvc.perform(post("/items/999/add-to-cart"))
                .andExpect(status().isInternalServerError());
    }
} 