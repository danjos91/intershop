package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.OrderItem;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

   @Mock
   private OrderRepository orderRepository;

   @Mock
   private ItemService itemService;

   @InjectMocks
   private OrderService orderService;

   private User user;
   private Item laptop;
   private Item smartphone;
   private Order order;
   private Map<Long, Integer> cartItems;

   @BeforeEach
   void setUp() {
       user = new User();
       user.setId(1L);
       user.setUsername("testuser");

       laptop = new Item();
       laptop.setId(1L);
       laptop.setTitle("Laptop");
       laptop.setPrice(999.99);

       smartphone = new Item();
       smartphone.setId(2L);
       smartphone.setTitle("Smartphone");
       smartphone.setPrice(599.99);

       order = new Order();
       order.setId(1L);
       order.setUserId(user.getId());
       order.setOrderDate(LocalDateTime.now());
       order.setStatus("PROCESSING");

       cartItems = new HashMap<>();
       cartItems.put(1L, 2);
       cartItems.put(2L, 1);
   }

   @Test
   void createOrderFromCart_WithValidItems_ShouldCreateOrder() {
       when(itemService.getItemById(1L)).thenReturn(Mono.just(laptop));
       when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));
       when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
           Order savedOrder = invocation.getArgument(0);
           savedOrder.setId(1L);
           return Mono.just(savedOrder);
       });

       Mono<Order> resultMono = orderService.createOrderFromCart(cartItems, user);

       StepVerifier.create(resultMono)
               .assertNext(result -> {
                   assertThat(result).isNotNull();
                   assertThat(result.getUserId()).isEqualTo(user.getId());
                   assertThat(result.getStatus()).isEqualTo("PROCESSING");
                   assertThat(result.getItems()).hasSize(2);

                   assertThat(result.getOrderDate()).isNotNull();
                   assertThat(result.getOrderDate()).isBeforeOrEqualTo(LocalDateTime.now());
                   assertThat(result.getOrderDate()).isAfter(LocalDateTime.now().minusSeconds(5));

                   List<OrderItem> orderItems = result.getItems();
                   assertThat(orderItems.get(0).getItem()).isEqualTo(laptop);
                   assertThat(orderItems.get(0).getQuantity()).isEqualTo(2);
                   assertThat(orderItems.get(0).getPrice()).isEqualTo(999.99);

                   assertThat(orderItems.get(1).getItem()).isEqualTo(smartphone);
                   assertThat(orderItems.get(1).getQuantity()).isEqualTo(1);
                   assertThat(orderItems.get(1).getPrice()).isEqualTo(599.99);
               })
               .verifyComplete();

       verify(itemService, times(2)).getItemById(any());
       verify(orderRepository).save(any(Order.class));
   }

   @Test
   void createOrderFromCart_EmptyCart_ShouldCreateEmptyOrder() {
       Map<Long, Integer> emptyCart = new HashMap<>();
       when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));

       Mono<Order> resultMono = orderService.createOrderFromCart(emptyCart, user);

       StepVerifier.create(resultMono)
               .assertNext(result -> {
                   assertThat(result).isNotNull();
                   assertThat(result.getItems()).isEmpty();
               })
               .verifyComplete();

       verify(orderRepository).save(any(Order.class));
   }

   @Test
   void getUserOrders_WithValidUser_ShouldReturnOrders() {
       Flux<Order> expectedOrders = Flux.just(order);
       when(orderRepository.findByUserId(user.getId())).thenReturn(expectedOrders);

       Flux<Order> resultFlux = orderService.getUserOrders(user);

       StepVerifier.create(resultFlux)
               .assertNext(result -> {
                   assertThat(result).isNotNull();
                   assertThat(result).isEqualTo(order);
               })
               .verifyComplete();

       verify(orderRepository).findByUserId(user.getId());
   }

   @Test
   void getUserOrders_WithNoOrders_ShouldReturnEmptyFlux() {
       when(orderRepository.findByUserId(user.getId())).thenReturn(Flux.empty());

       Flux<Order> resultFlux = orderService.getUserOrders(user);

       StepVerifier.create(resultFlux)
               .verifyComplete();

       verify(orderRepository).findByUserId(user.getId());
   }

   @Test
   void getOrderById_WithValidId_ShouldReturnOrder() {
       Long orderId = 1L;
       when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));

       Mono<Order> resultMono = orderService.getOrderById(orderId);

       StepVerifier.create(resultMono)
               .assertNext(result -> {
                   assertThat(result).isNotNull();
                   assertThat(result.getId()).isEqualTo(orderId);
               })
               .verifyComplete();

       verify(orderRepository).findById(orderId);
   }

   @Test
   void getOrderById_WithInvalidId_ShouldThrowException() {
       Long orderId = 999L;
       when(orderRepository.findById(orderId)).thenReturn(Mono.empty());

       Mono<Order> resultMono = orderService.getOrderById(orderId);

       StepVerifier.create(resultMono)
               .expectError(RuntimeException.class)
               .verify();

       verify(orderRepository).findById(orderId);
   }

   @Test
   void createOrderFromCart_ShouldCalculateCorrectTotal() {
       when(itemService.getItemById(1L)).thenReturn(Mono.just(laptop));
       when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));
       when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
           Order savedOrder = invocation.getArgument(0);
           savedOrder.setId(1L);
           return Mono.just(savedOrder);
       });

       Mono<Order> resultMono = orderService.createOrderFromCart(cartItems, user);

       StepVerifier.create(resultMono)
               .assertNext(result -> {
                   double expectedTotal = (999.99 * 2) + (599.99 * 1);
                   assertThat(result.getTotalSum()).isEqualTo(expectedTotal);
               })
               .verifyComplete();
   }
} 