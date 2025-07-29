package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private Item laptop;
    private Item smartphone;
    private List<Item> items;

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

        items = Arrays.asList(laptop, smartphone);
    }

    @Test
    void searchItems_WithQuery_ShouldReturnFilteredResults() {
        // Given
        String query = "laptop";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> expectedPage = new PageImpl<>(List.of(laptop), pageable, 1);
        
        when(itemRepository.findByTitleContainingIgnoreCase(query, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Item> result = itemService.searchItems(query, 1, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
        verify(itemRepository).findByTitleContainingIgnoreCase(query, pageable);
    }

    @Test
    void searchItems_WithAlphaSort_ShouldReturnSortedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> expectedPage = new PageImpl<>(items, pageable, 2);
        
        when(itemRepository.findByOrderByTitleAsc(pageable))
                .thenReturn(expectedPage);

        // When
        Page<Item> result = itemService.searchItems(null, 1, 10, "ALPHA");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(itemRepository).findByOrderByTitleAsc(pageable);
    }

    @Test
    void searchItems_WithPriceSort_ShouldReturnSortedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> expectedPage = new PageImpl<>(items, pageable, 2);
        
        when(itemRepository.findByOrderByPriceAsc(pageable))
                .thenReturn(expectedPage);

        // When
        Page<Item> result = itemService.searchItems(null, 1, 10, "PRICE");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(itemRepository).findByOrderByPriceAsc(pageable);
    }

    @Test
    void searchItems_WithoutQueryAndSort_ShouldReturnAllItems() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> expectedPage = new PageImpl<>(items, pageable, 2);
        
        when(itemRepository.findAll(pageable))
                .thenReturn(expectedPage);

        // When
        Page<Item> result = itemService.searchItems(null, 1, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        verify(itemRepository).findAll(pageable);
    }

    @Test
    void getItemById_WithValidId_ShouldReturnItem() {
        // Given
        Long itemId = 1L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(laptop));

        // When
        Item result = itemService.getItemById(itemId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getTitle()).isEqualTo("Laptop");
        verify(itemRepository).findById(itemId);
    }

    @Test
    void getItemById_WithInvalidId_ShouldThrowException() {
        // Given
        Long itemId = 999L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> itemService.getItemById(itemId))
                .isInstanceOf(RuntimeException.class);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void deleteItem_WithValidId_ShouldDeleteItem() {
        // Given
        Long itemId = 1L;
        doNothing().when(itemRepository).deleteById(itemId);

        // When
        itemService.deleteItem(itemId);

        // Then
        verify(itemRepository).deleteById(itemId);
    }
} 