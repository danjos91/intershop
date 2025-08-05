package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.service.CartService;
import io.github.danjos.intershop.service.OrderService;
import io.github.danjos.intershop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(WebCartController.class)
@DisplayName("WebCartController Tests")
class WebCartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    private Item laptop;
    private User user;
    private Order order;
    private List<CartItemDto> cartItems;

    @BeforeEach
    void setUp() {
        laptop = new Item();
        laptop.setId(1L);
        laptop.setTitle("Laptop");
        laptop.setDescription("High performance laptop");
        laptop.setPrice(999.99);
        laptop.setStock(10);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        order = new Order();
        order.setId(1L);
        order.setUserId(user.getId());
        order.setStatus("PROCESSING");

        CartItemDto cartItem = new CartItemDto(laptop, 2);
        cartItems = Arrays.asList(cartItem);
    }

    @Nested
    @DisplayName("Show Cart Tests")
    class ShowCartTests {

        @Test
        @DisplayName("Should return cart page with items")
        void showCart_WithItems_ShouldReturnCartPage() {
            when(cartService.getCartItemsReactive(any())).thenReturn(Mono.just(cartItems));
            when(cartService.getCartTotalReactive(any())).thenReturn(Mono.just(1999.98));
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));

            webTestClient.get()
                    .uri("/cart/items")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .consumeWith(response -> {
                        String body = new String(response.getResponseBody());
                        assert body.contains("cart");
                    });
        }

        @Test
        @DisplayName("Should return cart page with empty cart")
        void showCart_WithEmptyCart_ShouldReturnCartPage() {
            when(cartService.getCartItemsReactive(any())).thenReturn(Mono.just(Arrays.asList()));
            when(cartService.getCartTotalReactive(any())).thenReturn(Mono.just(0.0));
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));

            webTestClient.get()
                    .uri("/cart/items")
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @DisplayName("Should handle service exceptions gracefully")
        void showCart_WithServiceException_ShouldRedirectToError() {
            when(cartService.getCartItemsReactive(any()))
                    .thenReturn(Mono.error(new RuntimeException("Service error")));

            webTestClient.get()
                    .uri("/cart/items")
                    .exchange()
                    .expectStatus().is3xxRedirection();
        }
    }

    @Nested
    @DisplayName("Handle Cart Action Tests")
    class HandleCartActionTests {

        @Test
        @DisplayName("Should handle invalid action")
        void handleCartAction_WithInvalidAction_ShouldHandleGracefully() {
            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/cart/items/1")
                            .queryParam("action", "invalid")
                            .build())
                    .exchange()
                    .expectStatus().is3xxRedirection();
        }

        @Test
        @DisplayName("Should handle non-numeric item ID")
        void handleCartAction_WithNonNumericId_ShouldReturnBadRequest() {
            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/cart/items/invalid")
                            .queryParam("action", "plus")
                            .build())
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order from cart")
        void createOrder_WithValidCart_ShouldCreateOrder() {
            Map<Long, Integer> cart = new HashMap<>();
            cart.put(1L, 2);

            when(cartService.getCart(any())).thenReturn(cart);
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));
            when(orderService.createOrderFromCart(cart, user)).thenReturn(Mono.just(order));

            webTestClient.post()
                    .uri("/buy")
                    .exchange()
                    .expectStatus().is3xxRedirection();
        }

        @Test
        @DisplayName("Should handle empty cart")
        void createOrder_WithEmptyCart_ShouldCreateOrder() {
            Map<Long, Integer> emptyCart = new HashMap<>();

            when(cartService.getCart(any())).thenReturn(emptyCart);
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));
            when(orderService.createOrderFromCart(emptyCart, user)).thenReturn(Mono.just(order));

            webTestClient.post()
                    .uri("/buy")
                    .exchange()
                    .expectStatus().is3xxRedirection();
        }
    }
} 