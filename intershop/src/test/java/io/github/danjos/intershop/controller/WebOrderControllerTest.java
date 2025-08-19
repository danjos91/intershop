package io.github.danjos.intershop.controller;

import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(WebOrderController.class)
@DisplayName("WebOrderController Tests")
class WebOrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    private User user;
    private Order order1;
    private Order order2;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        order1 = new Order();
        order1.setId(1L);
        order1.setUserId(user.getId());
        order1.setStatus("PROCESSING");

        order2 = new Order();
        order2.setId(2L);
        order2.setUserId(user.getId());
        order2.setStatus("COMPLETED");

        orders = Arrays.asList(order1, order2);
    }

    @Nested
    @DisplayName("Show Orders Tests")
    class ShowOrdersTests {

        @Test
        @DisplayName("Should return orders page with user orders")
        void showOrders_WithUserOrders_ShouldReturnOrdersPage() {
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));
            when(orderService.getUserOrders(user)).thenReturn(Flux.fromIterable(orders));

            webTestClient.get()
                    .uri("/orders")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .consumeWith(response -> {
                        String body = new String(response.getResponseBody());
                        assert body.contains("orders");
                    });
        }

        @Test
        @DisplayName("Should return orders page with empty orders")
        void showOrders_WithEmptyOrders_ShouldReturnOrdersPage() {
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));
            when(orderService.getUserOrders(user)).thenReturn(Flux.empty());

            webTestClient.get()
                    .uri("/orders")
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @DisplayName("Should handle user service error")
        void showOrders_WithUserServiceError_ShouldHandleGracefully() {
            when(userService.getCurrentUser())
                    .thenReturn(Mono.error(new RuntimeException("User service error")));

            webTestClient.get()
                    .uri("/orders")
                    .exchange()
                    .expectStatus().isEqualTo(500);
        }

        @Test
        @DisplayName("Should handle order service error")
        void showOrders_WithOrderServiceError_ShouldHandleGracefully() {
            when(userService.getCurrentUser()).thenReturn(Mono.just(user));
            when(orderService.getUserOrders(user))
                    .thenReturn(Flux.error(new RuntimeException("Order service error")));

            webTestClient.get()
                    .uri("/orders")
                    .exchange()
                    .expectStatus().isEqualTo(500);
        }
    }

    @Nested
    @DisplayName("Show Order Tests")
    class ShowOrderTests {

        @Test
        @DisplayName("Should return order page with valid ID")
        void showOrder_WithValidId_ShouldReturnOrderPage() {
            when(orderService.getOrderById(1L)).thenReturn(Mono.just(order1));

            webTestClient.get()
                    .uri("/orders/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .consumeWith(response -> {
                        String body = new String(response.getResponseBody());
                        assert body.contains("order");
                    });
        }

        @Test
        @DisplayName("Should return order page with new order parameter")
        void showOrder_WithNewOrderParameter_ShouldReturnOrderPage() {
            when(orderService.getOrderById(1L)).thenReturn(Mono.just(order1));

            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/orders/1")
                            .queryParam("newOrder", "true")
                            .build())
                    .exchange()
                    .expectStatus().isOk();
        }

        @Test
        @DisplayName("Should handle order not found")
        void showOrder_WithInvalidId_ShouldHandleGracefully() {
            when(orderService.getOrderById(999L))
                    .thenReturn(Mono.error(new RuntimeException("Order not found")));

            webTestClient.get()
                    .uri("/orders/999")
                    .exchange()
                    .expectStatus().isEqualTo(500);
        }

        @Test
        @DisplayName("Should handle non-numeric order ID")
        void showOrder_WithNonNumericId_ShouldReturnBadRequest() {
            webTestClient.get()
                    .uri("/orders/invalid")
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Should handle order service error")
        void showOrder_WithOrderServiceError_ShouldHandleGracefully() {
            when(orderService.getOrderById(1L))
                    .thenReturn(Mono.error(new RuntimeException("Order service error")));

            webTestClient.get()
                    .uri("/orders/1")
                    .exchange()
                    .expectStatus().isEqualTo(500);
        }
    }
} 