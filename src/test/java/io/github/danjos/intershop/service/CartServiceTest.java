package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ItemService itemService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CartService cartService;

    private Item laptop;
    private Item smartphone;
    private Map<Long, Integer> cart;

    @BeforeEach
    void setUp() {
        laptop = new Item();
        laptop.setId(1L);
        laptop.setTitle("Laptop");
        laptop.setDescription("High performance laptop");
        laptop.setPrice(999.99);

        smartphone = new Item();
        smartphone.setId(2L);
        smartphone.setTitle("Smartphone");
        smartphone.setDescription("Latest smartphone");
        smartphone.setPrice(599.99);

        cart = new HashMap<>();
        cart.put(1L, 2);
        cart.put(2L, 1);
    }

    @Test
    void addItemToCart_NewItem_ShouldAddToCart() {
        // Given
        Long itemId = 3L;
        when(session.getAttribute("cart")).thenReturn(cart);

        // When
        cartService.addItemToCart(itemId, session);

        // Then
        assertThat(cart.get(itemId)).isEqualTo(1);
        verify(session).setAttribute("cart", cart);
    }

    @Test
    void addItemToCart_ExistingItem_ShouldIncrementQuantity() {
        // Given
        Long itemId = 1L;
        when(session.getAttribute("cart")).thenReturn(cart);

        // When
        cartService.addItemToCart(itemId, session);

        // Then
        assertThat(cart.get(itemId)).isEqualTo(3);
        verify(session).setAttribute("cart", cart);
    }

    @Test
    void addItemToCart_EmptyCart_ShouldCreateNewCart() {
        // Given
        Long itemId = 1L;
        when(session.getAttribute("cart")).thenReturn(null);

        // When
        cartService.addItemToCart(itemId, session);

        // Then
        verify(session).setAttribute(eq("cart"), any(Map.class));
    }

    @Test
    void removeItemFromCart_QuantityGreaterThanOne_ShouldDecrementQuantity() {
        // Given
        Long itemId = 1L;
        when(session.getAttribute("cart")).thenReturn(cart);

        // When
        cartService.removeItemFromCart(itemId, session);

        // Then
        assertThat(cart.get(itemId)).isEqualTo(1);
        verify(session).setAttribute("cart", cart);
    }

    @Test
    void removeItemFromCart_QuantityEqualsOne_ShouldRemoveItem() {
        // Given
        Long itemId = 2L;
        when(session.getAttribute("cart")).thenReturn(cart);

        // When
        cartService.removeItemFromCart(itemId, session);

        // Then
        assertThat(cart).doesNotContainKey(itemId);
        verify(session).setAttribute("cart", cart);
    }

    @Test
    void getCart_ExistingCart_ShouldReturnCart() {
        // Given
        when(session.getAttribute("cart")).thenReturn(cart);

        // When
        Map<Long, Integer> result = cartService.getCart(session);

        // Then
        assertThat(result).isEqualTo(cart);
    }

    @Test
    void getCart_NoCart_ShouldCreateNewCart() {
        // Given
        when(session.getAttribute("cart")).thenReturn(null);

        // When
        Map<Long, Integer> result = cartService.getCart(session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(session).setAttribute(eq("cart"), any(Map.class));
    }

    @Test
    void getCartItems_WithItems_ShouldReturnCartItemDtos() {
        // Given
        when(session.getAttribute("cart")).thenReturn(cart);
        when(itemService.getItemById(1L)).thenReturn(laptop);
        when(itemService.getItemById(2L)).thenReturn(smartphone);

        // When
        List<CartItemDto> result = cartService.getCartItems(session);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCount()).isEqualTo(2);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getCount()).isEqualTo(1);
    }

    @Test
    void getCartTotal_WithItems_ShouldReturnCorrectTotal() {
        // Given
        when(session.getAttribute("cart")).thenReturn(cart);
        when(itemService.getItemById(1L)).thenReturn(laptop);
        when(itemService.getItemById(2L)).thenReturn(smartphone);

        // When
        double result = cartService.getCartTotal(session);

        // Then
        // Expected: (999.99 * 2) + (599.99 * 1) = 2599.97
        assertThat(result).isEqualTo(2599.97);
    }

    @Test
    void getCartTotal_EmptyCart_ShouldReturnZero() {
        // Given
        Map<Long, Integer> emptyCart = new HashMap<>();
        when(session.getAttribute("cart")).thenReturn(emptyCart);

        // When
        double result = cartService.getCartTotal(session);

        // Then
        assertThat(result).isEqualTo(0.0);
    }
} 