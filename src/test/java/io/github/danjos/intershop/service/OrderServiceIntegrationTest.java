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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item laptop;
    private Item smartphone;
    private Map<Long, Integer> cartItems;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        laptop = new Item();
        laptop.setTitle("Laptop");
        laptop.setDescription("High performance laptop");
        laptop.setPrice(999.99);
        laptop.setStock(10);
        laptop.setImgPath("/images/laptop.jpg");
        laptop = itemRepository.save(laptop);

        smartphone = new Item();
        smartphone.setTitle("Smartphone");
        smartphone.setDescription("Latest smartphone model");
        smartphone.setPrice(599.99);
        smartphone.setStock(15);
        smartphone.setImgPath("/images/smartphone.jpg");
        smartphone = itemRepository.save(smartphone);

        cartItems = new HashMap<>();
        cartItems.put(laptop.getId(), 2);
        cartItems.put(smartphone.getId(), 1);
    }

    @Test
    void createOrderFromCart_WithValidItems_ShouldCreateOrder() {
        Order result = orderService.createOrderFromCart(cartItems, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getStatus()).isEqualTo("PROCESSING");
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getTotalSum()).isCloseTo(2599.97, within(0.01));

        List<Order> savedOrders = orderRepository.findByUser(user);
        assertThat(savedOrders).hasSize(1);
        assertThat(savedOrders.get(0).getId()).isEqualTo(result.getId());
    }

    @Test
    void createOrderFromCart_EmptyCart_ShouldCreateEmptyOrder() {
        Map<Long, Integer> emptyCart = new HashMap<>();
        
        Order result = orderService.createOrderFromCart(emptyCart, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalSum()).isEqualTo(0.0);
    }

    @Test
    void getUserOrders_WithValidUser_ShouldReturnOrders() {
        Order order1 = orderService.createOrderFromCart(cartItems, user);
        
        Map<Long, Integer> cartItems2 = new HashMap<>();
        cartItems2.put(laptop.getId(), 1);
        Order order2 = orderService.createOrderFromCart(cartItems2, user);

        List<Order> result = orderService.getUserOrders(user);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("id").contains(order1.getId(), order2.getId());
    }

    @Test
    void getOrderById_WithValidId_ShouldReturnOrder() {
        Order savedOrder = orderService.createOrderFromCart(cartItems, user);

        Order result = orderService.getOrderById(savedOrder.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedOrder.getId());
        assertThat(result.getUser()).isEqualTo(user);
    }
} 