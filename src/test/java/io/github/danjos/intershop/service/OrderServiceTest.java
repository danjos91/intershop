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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
       order.setUser(user);
       order.setOrderDate(LocalDateTime.now());
       order.setStatus("PROCESSING");

       cartItems = new HashMap<>();
       cartItems.put(1L, 2); // 2 laptops
       cartItems.put(2L, 1); // 1 smartphone
   }

   @Test
   void createOrderFromCart_WithValidItems_ShouldCreateOrder() {
       // Given
       when(itemService.getItemById(1L)).thenReturn(laptop);
       when(itemService.getItemById(2L)).thenReturn(smartphone);
       when(orderRepository.save(any(Order.class))).thenReturn(order);

       // When
       Order result = orderService.createOrderFromCart(cartItems, user);

       // Then
       assertThat(result).isNotNull();
       assertThat(result.getUser()).isEqualTo(user);
       assertThat(result.getStatus()).isEqualTo("PROCESSING");
       assertThat(result.getItems()).hasSize(2);

       // Verify order items
       List<OrderItem> orderItems = result.getItems();
       assertThat(orderItems.get(0).getItem()).isEqualTo(laptop);
       assertThat(orderItems.get(0).getQuantity()).isEqualTo(2);
       assertThat(orderItems.get(0).getPrice()).isEqualTo(999.99);

       assertThat(orderItems.get(1).getItem()).isEqualTo(smartphone);
       assertThat(orderItems.get(1).getQuantity()).isEqualTo(1);
       assertThat(orderItems.get(1).getPrice()).isEqualTo(599.99);

       verify(itemService, times(2)).getItemById(any());
       verify(orderRepository).save(any(Order.class));
   }

   @Test
   void createOrderFromCart_EmptyCart_ShouldCreateEmptyOrder() {
       // Given
       Map<Long, Integer> emptyCart = new HashMap<>();
       when(orderRepository.save(any(Order.class))).thenReturn(order);

       // When
       Order result = orderService.createOrderFromCart(emptyCart, user);

       // Then
       assertThat(result).isNotNull();
       assertThat(result.getItems()).isEmpty();
       verify(orderRepository).save(any(Order.class));
   }

   @Test
   void getUserOrders_WithValidUser_ShouldReturnOrders() {
       // Given
       List<Order> expectedOrders = List.of(order);
       when(orderRepository.findByUser(user)).thenReturn(expectedOrders);

       // When
       List<Order> result = orderService.getUserOrders(user);

       // Then
       assertThat(result).isNotNull();
       assertThat(result).hasSize(1);
       assertThat(result.get(0)).isEqualTo(order);
       verify(orderRepository).findByUser(user);
   }

   @Test
   void getUserOrders_WithNoOrders_ShouldReturnEmptyList() {
       // Given
       when(orderRepository.findByUser(user)).thenReturn(List.of());

       // When
       List<Order> result = orderService.getUserOrders(user);

       // Then
       assertThat(result).isNotNull();
       assertThat(result).isEmpty();
       verify(orderRepository).findByUser(user);
   }

   @Test
   void getOrderById_WithValidId_ShouldReturnOrder() {
       // Given
       Long orderId = 1L;
       when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

       // When
       Order result = orderService.getOrderById(orderId);

       // Then
       assertThat(result).isNotNull();
       assertThat(result.getId()).isEqualTo(orderId);
       verify(orderRepository).findById(orderId);
   }

   @Test
   void getOrderById_WithInvalidId_ShouldThrowException() {
       // Given
       Long orderId = 999L;
       when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

       // When & Then
       assertThatThrownBy(() -> orderService.getOrderById(orderId))
               .isInstanceOf(RuntimeException.class);
       verify(orderRepository).findById(orderId);
   }

   @Test
   void createOrderFromCart_ShouldSetCorrectOrderDate() {
       // Given
       when(itemService.getItemById(1L)).thenReturn(laptop);
       when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
           Order savedOrder = invocation.getArgument(0);
           savedOrder.setId(1L);
           return savedOrder;
       });

       // When
       Order result = orderService.createOrderFromCart(cartItems, user);

       // Then
       assertThat(result.getOrderDate()).isNotNull();
       assertThat(result.getOrderDate()).isBeforeOrEqualTo(LocalDateTime.now());
       assertThat(result.getOrderDate()).isAfter(LocalDateTime.now().minusSeconds(5));
   }

   @Test
   void createOrderFromCart_ShouldCalculateCorrectTotal() {
       // Given
       when(itemService.getItemById(1L)).thenReturn(laptop);
       when(itemService.getItemById(2L)).thenReturn(smartphone);
       when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
           Order savedOrder = invocation.getArgument(0);
           savedOrder.setId(1L);
           return savedOrder;
       });

       // When
       Order result = orderService.createOrderFromCart(cartItems, user);

       // Then
       double expectedTotal = (999.99 * 2) + (599.99 * 1); // 2599.97
       assertThat(result.getTotalSum()).isEqualTo(expectedTotal);
   }
} 