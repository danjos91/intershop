package io.github.danjos.intershop.service;

import io.github.danjos.intershop.dto.CartItemDto;
import io.github.danjos.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Tests")
class CartServiceTest {

   @Mock
   private ItemService itemService;

   @Mock
   private WebSession session;

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

   @Nested
   @DisplayName("Add Item To Cart Tests")
   class AddItemToCartTests {

       @Test
       @DisplayName("Should add new item to cart")
       void addItemToCart_NewItem_ShouldAddToCart() {
           Long itemId = 3L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           cartService.addItemToCart(itemId, session);

           assertThat(cart.get(itemId)).isEqualTo(1);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should increment quantity for existing item")
       void addItemToCart_ExistingItem_ShouldIncrementQuantity() {
           Long itemId = 1L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           cartService.addItemToCart(itemId, session);

           assertThat(cart.get(itemId)).isEqualTo(3);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should create new cart when cart is empty")
       void addItemToCart_EmptyCart_ShouldCreateNewCartAndUpdateCartAttribute() {
           Long itemId = 1L;
           when(session.getAttributes().get("cart")).thenReturn(null);

           cartService.addItemToCart(itemId, session);

           verify(session.getAttributes(), times(2)).put(eq("cart"), any(Map.class));
       }

       @Test
       @DisplayName("Should handle null session gracefully")
       void addItemToCart_WithNullSession_ShouldHandleGracefully() {
           Long itemId = 1L;

           cartService.addItemToCart(itemId, null);

           // Should not throw exception
       }
   }

   @Nested
   @DisplayName("Add Item To Cart Reactive Tests")
   class AddItemToCartReactiveTests {

       @Test
       @DisplayName("Should add item to cart reactively")
       void addItemToCartReactive_NewItem_ShouldAddToCart() {
           Long itemId = 3L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Mono<Void> result = cartService.addItemToCartReactive(itemId, session);

           StepVerifier.create(result)
                   .verifyComplete();

           assertThat(cart.get(itemId)).isEqualTo(1);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should increment quantity for existing item reactively")
       void addItemToCartReactive_ExistingItem_ShouldIncrementQuantity() {
           Long itemId = 1L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Mono<Void> result = cartService.addItemToCartReactive(itemId, session);

           StepVerifier.create(result)
                   .verifyComplete();

           assertThat(cart.get(itemId)).isEqualTo(3);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should handle null session reactively")
       void addItemToCartReactive_WithNullSession_ShouldHandleGracefully() {
           Long itemId = 1L;

           Mono<Void> result = cartService.addItemToCartReactive(itemId, null);

           StepVerifier.create(result)
                   .verifyComplete();
       }
   }

   @Nested
   @DisplayName("Remove Item From Cart Tests")
   class RemoveItemFromCartTests {

       @Test
       @DisplayName("Should decrement quantity when quantity greater than one")
       void removeItemFromCart_QuantityGreaterThanOne_ShouldDecrementQuantity() {
           Long itemId = 1L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           cartService.removeItemFromCart(itemId, session);

           assertThat(cart.get(itemId)).isEqualTo(1);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should remove item when quantity equals one")
       void removeItemFromCart_QuantityEqualsOne_ShouldRemoveItem() {
           Long itemId = 2L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           cartService.removeItemFromCart(itemId, session);

           assertThat(cart).doesNotContainKey(itemId);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should handle removing non-existent item")
       void removeItemFromCart_NonExistentItem_ShouldHandleGracefully() {
           Long itemId = 999L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           cartService.removeItemFromCart(itemId, session);

           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should handle null session gracefully")
       void removeItemFromCart_WithNullSession_ShouldHandleGracefully() {
           Long itemId = 1L;

           cartService.removeItemFromCart(itemId, null);

           // Should not throw exception
       }
   }

   @Nested
   @DisplayName("Remove Item From Cart Reactive Tests")
   class RemoveItemFromCartReactiveTests {

       @Test
       @DisplayName("Should remove item from cart reactively")
       void removeItemFromCartReactive_ExistingItem_ShouldRemoveItem() {
           Long itemId = 2L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Mono<Void> result = cartService.removeItemFromCartReactive(itemId, session);

           StepVerifier.create(result)
                   .verifyComplete();

           assertThat(cart).doesNotContainKey(itemId);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should handle non-existent item reactively")
       void removeItemFromCartReactive_NonExistentItem_ShouldHandleGracefully() {
           Long itemId = 999L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Mono<Void> result = cartService.removeItemFromCartReactive(itemId, session);

           StepVerifier.create(result)
                   .verifyComplete();
       }

       @Test
       @DisplayName("Should handle null session reactively")
       void removeItemFromCartReactive_WithNullSession_ShouldHandleGracefully() {
           Long itemId = 1L;

           Mono<Void> result = cartService.removeItemFromCartReactive(itemId, null);

           StepVerifier.create(result)
                   .verifyComplete();
       }
   }

   @Nested
   @DisplayName("Delete Item From Cart Reactive Tests")
   class DeleteItemFromCartReactiveTests {

       @Test
       @DisplayName("Should delete item from cart reactively")
       void deleteItemFromCartReactive_ExistingItem_ShouldDeleteItem() {
           Long itemId = 1L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Mono<Void> result = cartService.deleteItemFromCartReactive(itemId, session);

           StepVerifier.create(result)
                   .verifyComplete();

           assertThat(cart).doesNotContainKey(itemId);
           verify(session.getAttributes()).put("cart", cart);
       }

       @Test
       @DisplayName("Should handle non-existent item reactively")
       void deleteItemFromCartReactive_NonExistentItem_ShouldHandleGracefully() {
           Long itemId = 999L;
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Mono<Void> result = cartService.deleteItemFromCartReactive(itemId, session);

           StepVerifier.create(result)
                   .verifyComplete();
       }
   }

   @Nested
   @DisplayName("Get Cart Tests")
   class GetCartTests {

       @Test
       @DisplayName("Should return existing cart")
       void getCart_ExistingCart_ShouldReturnCart() {
           when(session.getAttributes().get("cart")).thenReturn(cart);

           Map<Long, Integer> result = cartService.getCart(session);

           assertThat(result).isEqualTo(cart);
       }

       @Test
       @DisplayName("Should create new cart when no cart exists")
       void getCart_NoCart_ShouldCreateNewCart() {
           when(session.getAttributes().get("cart")).thenReturn(null);

           Map<Long, Integer> result = cartService.getCart(session);

           assertThat(result).isNotNull();
           assertThat(result).isEmpty();
           verify(session.getAttributes()).put(eq("cart"), any(Map.class));
       }

       @Test
       @DisplayName("Should handle null session gracefully")
       void getCart_WithNullSession_ShouldReturnEmptyMap() {
           Map<Long, Integer> result = cartService.getCart(null);

           assertThat(result).isNotNull();
           assertThat(result).isEmpty();
       }
   }

   @Nested
   @DisplayName("Get Cart Items Tests")
   class GetCartItemsTests {

       @Test
       @DisplayName("Should return cart item DTOs with items")
       void getCartItems_WithItems_ShouldReturnCartItemDtos() {
           when(session.getAttributes().get("cart")).thenReturn(cart);
           when(itemService.getItemById(1L)).thenReturn(Mono.just(laptop));
           when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));

           List<CartItemDto> result = cartService.getCartItems(session);

           assertThat(result).hasSize(2);
           assertThat(result.get(0).getId()).isEqualTo(1L);
           assertThat(result.get(0).getCount()).isEqualTo(2);
           assertThat(result.get(1).getId()).isEqualTo(2L);
           assertThat(result.get(1).getCount()).isEqualTo(1);
       }

       @Test
       @DisplayName("Should return empty list for empty cart")
       void getCartItems_EmptyCart_ShouldReturnEmptyList() {
           Map<Long, Integer> emptyCart = new HashMap<>();
           when(session.getAttributes().get("cart")).thenReturn(emptyCart);

           List<CartItemDto> result = cartService.getCartItems(session);

           assertThat(result).isEmpty();
       }

       @Test
       @DisplayName("Should handle null session gracefully")
       void getCartItems_WithNullSession_ShouldReturnEmptyList() {
           List<CartItemDto> result = cartService.getCartItems(null);

           assertThat(result).isEmpty();
       }
   }

   @Nested
   @DisplayName("Get Cart Items Reactive Tests")
   class GetCartItemsReactiveTests {

       @Test
       @DisplayName("Should return cart items reactively")
       void getCartItemsReactive_WithItems_ShouldReturnCartItemDtos() {
           when(session.getAttributes().get("cart")).thenReturn(cart);
           when(itemService.getItemById(1L)).thenReturn(Mono.just(laptop));
           when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));

           Mono<List<CartItemDto>> result = cartService.getCartItemsReactive(session);

           StepVerifier.create(result)
                   .assertNext(items -> {
                       assertThat(items).hasSize(2);
                       assertThat(items.get(0).getId()).isEqualTo(1L);
                       assertThat(items.get(0).getCount()).isEqualTo(2);
                       assertThat(items.get(1).getId()).isEqualTo(2L);
                       assertThat(items.get(1).getCount()).isEqualTo(1);
                   })
                   .verifyComplete();
       }

       @Test
       @DisplayName("Should return empty list for empty cart reactively")
       void getCartItemsReactive_EmptyCart_ShouldReturnEmptyList() {
           Map<Long, Integer> emptyCart = new HashMap<>();
           when(session.getAttributes().get("cart")).thenReturn(emptyCart);

           Mono<List<CartItemDto>> result = cartService.getCartItemsReactive(session);

           StepVerifier.create(result)
                   .assertNext(items -> assertThat(items).isEmpty())
                   .verifyComplete();
       }

       @Test
       @DisplayName("Should handle null session reactively")
       void getCartItemsReactive_WithNullSession_ShouldReturnEmptyList() {
           Mono<List<CartItemDto>> result = cartService.getCartItemsReactive(null);

           StepVerifier.create(result)
                   .assertNext(items -> assertThat(items).isEmpty())
                   .verifyComplete();
       }
   }

   @Nested
   @DisplayName("Get Cart Total Tests")
   class GetCartTotalTests {

       @Test
       @DisplayName("Should return correct total for items in cart")
       void getCartTotal_WithItems_ShouldReturnCorrectTotal() {
           when(session.getAttributes().get("cart")).thenReturn(cart);
           when(itemService.getItemById(1L)).thenReturn(Mono.just(laptop));
           when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));

           double result = cartService.getCartTotal(session);

           assertThat(result).isCloseTo(2599.97, within(0.01));
       }

       @Test
       @DisplayName("Should return zero for empty cart")
       void getCartTotal_EmptyCart_ShouldReturnZero() {
           Map<Long, Integer> emptyCart = new HashMap<>();
           when(session.getAttributes().get("cart")).thenReturn(emptyCart);

           double result = cartService.getCartTotal(session);

           assertThat(result).isEqualTo(0.0);
       }

       @Test
       @DisplayName("Should return zero for null session")
       void getCartTotal_WithNullSession_ShouldReturnZero() {
           double result = cartService.getCartTotal(null);

           assertThat(result).isEqualTo(0.0);
       }

       @Test
       @DisplayName("Should handle missing items gracefully")
       void getCartTotal_WithMissingItems_ShouldHandleGracefully() {
           when(session.getAttributes().get("cart")).thenReturn(cart);
           when(itemService.getItemById(1L)).thenReturn(Mono.empty());
           when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));

           double result = cartService.getCartTotal(session);

           assertThat(result).isCloseTo(599.99, within(0.01));
       }
   }

   @Nested
   @DisplayName("Get Cart Total Reactive Tests")
   class GetCartTotalReactiveTests {

       @Test
       @DisplayName("Should return correct total reactively")
       void getCartTotalReactive_WithItems_ShouldReturnCorrectTotal() {
           when(session.getAttributes().get("cart")).thenReturn(cart);
           when(itemService.getItemById(1L)).thenReturn(Mono.just(laptop));
           when(itemService.getItemById(2L)).thenReturn(Mono.just(smartphone));

           Mono<Double> result = cartService.getCartTotalReactive(session);

           StepVerifier.create(result)
                   .assertNext(total -> assertThat(total).isCloseTo(2599.97, within(0.01)))
                   .verifyComplete();
       }

       @Test
       @DisplayName("Should return zero for empty cart reactively")
       void getCartTotalReactive_EmptyCart_ShouldReturnZero() {
           Map<Long, Integer> emptyCart = new HashMap<>();
           when(session.getAttributes().get("cart")).thenReturn(emptyCart);

           Mono<Double> result = cartService.getCartTotalReactive(session);

           StepVerifier.create(result)
                   .assertNext(total -> assertThat(total).isEqualTo(0.0))
                   .verifyComplete();
       }

       @Test
       @DisplayName("Should return zero for null session reactively")
       void getCartTotalReactive_WithNullSession_ShouldReturnZero() {
           Mono<Double> result = cartService.getCartTotalReactive(null);

           StepVerifier.create(result)
                   .assertNext(total -> assertThat(total).isEqualTo(0.0))
                   .verifyComplete();
       }
   }
} 