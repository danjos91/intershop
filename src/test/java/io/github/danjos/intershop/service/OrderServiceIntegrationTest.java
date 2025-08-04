package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.repository.ItemRepository;
import io.github.danjos.intershop.repository.OrderRepository;
import io.github.danjos.intershop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item laptop;
    private Item smartphone;
    private Map<Long, Integer> cartItems;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll()
                .then(itemRepository.deleteAll())
                .block();

        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        laptop = new Item();
        laptop.setTitle("Laptop");
        laptop.setDescription("High performance laptop");
        laptop.setPrice(999.99);
        laptop.setStock(10);
        laptop.setImgPath("/images/laptop.jpg");
        laptop = itemRepository.save(laptop).block();

        smartphone = new Item();
        smartphone.setTitle("Smartphone");
        smartphone.setDescription("Latest smartphone model");
        smartphone.setPrice(599.99);
        smartphone.setStock(15);
        smartphone.setImgPath("/images/smartphone.jpg");
        smartphone = itemRepository.save(smartphone).block();

        cartItems = new HashMap<>();
        cartItems.put(laptop.getId(), 2);
        cartItems.put(smartphone.getId(), 1);
    }

    @Test
    void createOrderFromCart_WithValidItems_ShouldCreateOrder() {
        Mono<Order> resultMono = orderService.createOrderFromCart(cartItems, user);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getUserId()).isEqualTo(user.getId());
                    assertThat(result.getStatus()).isEqualTo("PROCESSING");
                    assertThat(result.getItems()).hasSize(2);
                    assertThat(result.getTotalSum()).isCloseTo(2599.97, within(0.01));
                })
                .verifyComplete();

        Flux<Order> savedOrders = orderRepository.findByUserId(user.getId());
        StepVerifier.create(savedOrders)
                .assertNext(savedOrder -> {
                    assertThat(savedOrder).isNotNull();
                    assertThat(savedOrder.getUserId()).isEqualTo(user.getId());
                })
                .verifyComplete();
    }

    @Test
    void createOrderFromCart_EmptyCart_ShouldCreateEmptyOrder() {
        Map<Long, Integer> emptyCart = new HashMap<>();

        Mono<Order> resultMono = orderService.createOrderFromCart(emptyCart, user);

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getId()).isNotNull();
                    assertThat(result.getItems()).isEmpty();
                    assertThat(result.getTotalSum()).isEqualTo(0.0);
                })
                .verifyComplete();
    }

    @Test
    void getUserOrders_WithValidUser_ShouldReturnOrders() {
        Order order1 = orderService.createOrderFromCart(cartItems, user).block();

        Map<Long, Integer> cartItems2 = new HashMap<>();
        cartItems2.put(laptop.getId(), 1);
        Order order2 = orderService.createOrderFromCart(cartItems2, user).block();

        Flux<Order> resultFlux = orderService.getUserOrders(user);

        StepVerifier.create(resultFlux)
                .assertNext(order -> {
                    assertThat(order).isNotNull();
                    assertThat(order.getUserId()).isEqualTo(user.getId());
                })
                .assertNext(order -> {
                    assertThat(order).isNotNull();
                    assertThat(order.getUserId()).isEqualTo(user.getId());
                })
                .verifyComplete();
    }

    @Test
    void getOrderById_WithValidId_ShouldReturnOrder() {
        Order savedOrder = orderService.createOrderFromCart(cartItems, user).block();

        Mono<Order> resultMono = orderService.getOrderById(savedOrder.getId());

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getId()).isEqualTo(savedOrder.getId());
                    assertThat(result.getUserId()).isEqualTo(user.getId());
                })
                .verifyComplete();
    }
} 