package io.github.danjos.intershop.service;

import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService Tests")
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

   @Nested
   @DisplayName("Search Items Tests")
   class SearchItemsTests {

       @Test
       @DisplayName("Should return sorted results when ALPHA sort is specified")
       void searchItems_WithAlphaSort_ShouldReturnSortedResults() {
           Flux<Item> itemsFlux = Flux.fromIterable(items);
           Mono<Long> countMono = Mono.just(2L);

           when(itemRepository.findByOrderByTitleAsc(10, 0))
                   .thenReturn(itemsFlux);
           when(itemRepository.countAll())
                   .thenReturn(countMono);

           Mono<Page<Item>> resultMono = itemService.searchItems(null, 1, 10, "ALPHA");

           StepVerifier.create(resultMono)
                   .assertNext(page -> {
                       assertThat(page).isNotNull();
                       assertThat(page.getContent()).hasSize(2);
                       assertThat(page.getTotalElements()).isEqualTo(2L);
                   })
                   .verifyComplete();

           verify(itemRepository).findByOrderByTitleAsc(10, 0);
           verify(itemRepository).countAll();
       }

       @Test
       @DisplayName("Should return sorted results when PRICE sort is specified")
       void searchItems_WithPriceSort_ShouldReturnSortedResults() {
           Flux<Item> itemsFlux = Flux.fromIterable(items);
           Mono<Long> countMono = Mono.just(2L);

           when(itemRepository.findByOrderByPriceAsc(10, 0))
                   .thenReturn(itemsFlux);
           when(itemRepository.countAll())
                   .thenReturn(countMono);

           Mono<Page<Item>> resultMono = itemService.searchItems(null, 1, 10, "PRICE");

           StepVerifier.create(resultMono)
                   .assertNext(page -> {
                       assertThat(page).isNotNull();
                       assertThat(page.getContent()).hasSize(2);
                       assertThat(page.getTotalElements()).isEqualTo(2L);
                   })
                   .verifyComplete();

           verify(itemRepository).findByOrderByPriceAsc(10, 0);
           verify(itemRepository).countAll();
       }

       @Test
       @DisplayName("Should return paginated results when no query and sort specified")
       void searchItems_WithoutQueryAndSort_ShouldReturnPaginatedResults() {
           Flux<Item> itemsFlux = Flux.fromIterable(items);
           Mono<Long> countMono = Mono.just(2L);

           when(itemRepository.findAllByOrderByIdAsc(10, 0))
                   .thenReturn(itemsFlux);
           when(itemRepository.countAll())
                   .thenReturn(countMono);

           Mono<Page<Item>> resultMono = itemService.searchItems(null, 1, 10, null);

           StepVerifier.create(resultMono)
                   .assertNext(page -> {
                       assertThat(page).isNotNull();
                       assertThat(page.getContent()).hasSize(2);
                       assertThat(page.getTotalElements()).isEqualTo(2L);
                   })
                   .verifyComplete();

           verify(itemRepository).findAllByOrderByIdAsc(10, 0);
           verify(itemRepository).countAll();
       }
   }

   @Nested
   @DisplayName("Get Item By ID Tests")
   class GetItemByIdTests {

       @Test
       @DisplayName("Should return item when valid ID is provided")
       void getItemById_WithValidId_ShouldReturnItem() {
           Long itemId = 1L;
           when(itemRepository.findById(itemId))
                   .thenReturn(Mono.just(laptop));

           Mono<Item> resultMono = itemService.getItemById(itemId);

           StepVerifier.create(resultMono)
                   .assertNext(item -> {
                       assertThat(item).isNotNull();
                       assertThat(item.getId()).isEqualTo(itemId);
                       assertThat(item.getTitle()).isEqualTo("Laptop");
                   })
                   .verifyComplete();

           verify(itemRepository).findById(itemId);
       }

       @Test
       @DisplayName("Should throw NotFoundException when invalid ID is provided")
       void getItemById_WithInvalidId_ShouldThrowException() {
           Long itemId = 999L;
           when(itemRepository.findById(itemId))
                   .thenReturn(Mono.empty());

           Mono<Item> resultMono = itemService.getItemById(itemId);

           StepVerifier.create(resultMono)
                   .expectError(NotFoundException.class)
                   .verify();

           verify(itemRepository).findById(itemId);
       }

       @Test
       @DisplayName("Should handle repository errors")
       void getItemById_WithRepositoryError_ShouldPropagateError() {
           Long itemId = 1L;
           when(itemRepository.findById(itemId))
                   .thenReturn(Mono.error(new RuntimeException("Database error")));

           Mono<Item> resultMono = itemService.getItemById(itemId);

           StepVerifier.create(resultMono)
                   .expectError(RuntimeException.class)
                   .verify();
       }
   }
} 