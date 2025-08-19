package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.model.Order;
import io.github.danjos.intershop.model.OrderItem;
import io.github.danjos.intershop.model.User;
import io.github.danjos.intershop.repository.OrderRepository;
import io.github.danjos.intershop.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

   @Mock
   private OrderRepository orderRepository;

   @Mock
   private OrderItemRepository orderItemRepository;

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

   @Nested
   @DisplayName("Create Order From Cart Tests")
   class CreateOrderFromCartTests {

       @Test
       @DisplayName("Should create order with valid items")
       void createOrderFromCart_WithValidItems_ShouldCreateOrder() {
           when(itemService.getItemByIds(any())).thenReturn(Flux.fromIterable(Arrays.asList(laptop, smartphone)));
           when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
               Order savedOrder = invocation.getArgument(0);
               savedOrder.setId(1L);
               return Mono.just(savedOrder);
           });
           when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
               OrderItem savedOrderItem = invocation.getArgument(0);
               savedOrderItem.setId(1L);
               return Mono.just(savedOrderItem);
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

           verify(itemService).getItemByIds(any());
           verify(orderRepository).save(any(Order.class));
           verify(orderItemRepository, times(2)).save(any(OrderItem.class));
       }

       @Test
       @DisplayName("Should create empty order when cart is empty")
       void createOrderFromCart_EmptyCart_ShouldCreateEmptyOrder() {
           Map<Long, Integer> emptyCart = new HashMap<>();
           when(itemService.getItemByIds(any())).thenReturn(Flux.empty());
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
       @DisplayName("Should calculate correct total for order")
       void createOrderFromCart_ShouldCalculateCorrectTotal() {
           when(itemService.getItemByIds(any())).thenReturn(Flux.fromIterable(Arrays.asList(laptop, smartphone)));
           when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
               Order savedOrder = invocation.getArgument(0);
               savedOrder.setId(1L);
               return Mono.just(savedOrder);
           });
           when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
               OrderItem savedOrderItem = invocation.getArgument(0);
               savedOrderItem.setId(1L);
               return Mono.just(savedOrderItem);
           });

           Mono<Order> resultMono = orderService.createOrderFromCart(cartItems, user);

           StepVerifier.create(resultMono)
                   .assertNext(result -> {
                       double expectedTotal = (999.99 * 2) + (599.99 * 1);
                       assertThat(result.getTotalSum()).isEqualTo(expectedTotal);
                   })
                   .verifyComplete();
       }

       @Test
       @DisplayName("Should handle missing items gracefully")
       void createOrderFromCart_WithMissingItems_ShouldHandleGracefully() {
           when(itemService.getItemByIds(any())).thenReturn(Flux.fromIterable(Arrays.asList(smartphone))); // Only return smartphone, laptop is missing
           when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
               Order savedOrder = invocation.getArgument(0);
               savedOrder.setId(1L);
               return Mono.just(savedOrder);
           });
           when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
               OrderItem savedOrderItem = invocation.getArgument(0);
               savedOrderItem.setId(1L);
               return Mono.just(savedOrderItem);
           });

           Mono<Order> resultMono = orderService.createOrderFromCart(cartItems, user);

           StepVerifier.create(resultMono)
                   .assertNext(result -> {
                       assertThat(result).isNotNull();
                       assertThat(result.getItems()).hasSize(1);
                       assertThat(result.getItems().get(0).getItem()).isEqualTo(smartphone);
                   })
                   .verifyComplete();
       }
   }

   @Nested
   @DisplayName("Get User Orders Tests")
   class GetUserOrdersTests {

       @Test
       @DisplayName("Should return orders for valid user")
       void getUserOrders_WithValidUser_ShouldReturnOrders() {
           Flux<Order> expectedOrders = Flux.just(order);
           when(orderRepository.findByUserId(user.getId())).thenReturn(expectedOrders);
           when(orderItemRepository.findByOrderId(order.getId())).thenReturn(Flux.empty());
           when(itemService.getItemByIds(any())).thenReturn(Flux.empty());

           Flux<Order> resultFlux = orderService.getUserOrders(user);

           StepVerifier.create(resultFlux)
                   .assertNext(result -> {
                       assertThat(result).isNotNull();
                       assertThat(result.getId()).isEqualTo(order.getId());
                       assertThat(result.getItems()).isEmpty();
                   })
                   .verifyComplete();

           verify(orderRepository).findByUserId(user.getId());
           verify(orderItemRepository).findByOrderId(order.getId());
       }

       @Test
       @DisplayName("Should return empty flux when user has no orders")
       void getUserOrders_WithNoOrders_ShouldReturnEmptyFlux() {
           when(orderRepository.findByUserId(user.getId())).thenReturn(Flux.empty());

           Flux<Order> resultFlux = orderService.getUserOrders(user);

           StepVerifier.create(resultFlux)
                   .verifyComplete();

           verify(orderRepository).findByUserId(user.getId());
       }

       @Test
       @DisplayName("Should handle repository errors")
       void getUserOrders_WithRepositoryError_ShouldPropagateError() {
           when(orderRepository.findByUserId(user.getId())).thenReturn(Flux.error(new RuntimeException("Repository error")));

           Flux<Order> resultFlux = orderService.getUserOrders(user);

           StepVerifier.create(resultFlux)
                   .expectError(RuntimeException.class)
                   .verify();
       }
   }

   @Nested
   @DisplayName("Get Order By ID Tests")
   class GetOrderByIdTests {

       @Test
       @DisplayName("Should return order when valid ID is provided")
       void getOrderById_WithValidId_ShouldReturnOrder() {
           Long orderId = 1L;
           when(orderRepository.findById(orderId)).thenReturn(Mono.just(order));
           when(orderItemRepository.findByOrderId(orderId)).thenReturn(Flux.empty());
           when(itemService.getItemByIds(any())).thenReturn(Flux.empty());

           Mono<Order> resultMono = orderService.getOrderById(orderId);

           StepVerifier.create(resultMono)
                   .assertNext(result -> {
                       assertThat(result).isNotNull();
                       assertThat(result.getId()).isEqualTo(orderId);
                       assertThat(result.getItems()).isEmpty();
                   })
                   .verifyComplete();

           verify(orderRepository).findById(orderId);
           verify(orderItemRepository).findByOrderId(orderId);
       }

       @Test
       @DisplayName("Should throw exception when invalid ID is provided")
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
       @DisplayName("Should handle repository errors")
       void getOrderById_WithRepositoryError_ShouldPropagateError() {
           Long orderId = 1L;
           when(orderRepository.findById(orderId)).thenReturn(Mono.error(new RuntimeException("Repository error")));

           Mono<Order> resultMono = orderService.getOrderById(orderId);

           StepVerifier.create(resultMono)
                   .expectError(RuntimeException.class)
                   .verify();
       }
   }
} 