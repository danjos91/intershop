package io.github.danjos.intershop.service;

import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

   @Test
   void searchItems_WithQuery_ShouldReturnFilteredResults() {
       String query = "laptop";
       Flux<Item> itemsFlux = Flux.just(laptop);
       Mono<Long> countMono = Mono.just(1L);

       when(itemRepository.findByTitleContainingIgnoreCase(query, 10, 0))
               .thenReturn(itemsFlux);
       when(itemRepository.countByTitleContainingIgnoreCase(query))
               .thenReturn(countMono);

       Mono<Page<Item>> resultMono = itemService.searchItems(query, 1, 10, null);

       StepVerifier.create(resultMono)
               .assertNext(page -> {
                   assertThat(page).isNotNull();
                   assertThat(page.getContent()).hasSize(1);
                   assertThat(page.getContent().get(0).getTitle()).isEqualTo("Laptop");
                   assertThat(page.getTotalElements()).isEqualTo(1L);
               })
               .verifyComplete();

       verify(itemRepository).findByTitleContainingIgnoreCase(query, 10, 0);
       verify(itemRepository).countByTitleContainingIgnoreCase(query);
   }

   @Test
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
   void searchItems_WithoutQueryAndSort_ShouldReturnAllItems() {
       Flux<Item> itemsFlux = Flux.fromIterable(items);
       Mono<Long> countMono = Mono.just(2L);

       when(itemRepository.findAll(Sort.by("id")))
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

       verify(itemRepository).findAll(Sort.by("id"));
       verify(itemRepository).countAll();
   }

   @Test
   void searchItems_WithPagination_ShouldReturnCorrectPage() {
       String query = "laptop";
       Flux<Item> itemsFlux = Flux.just(laptop);
       Mono<Long> countMono = Mono.just(1L);

       when(itemRepository.findByTitleContainingIgnoreCase(query, 5, 5))
               .thenReturn(itemsFlux);
       when(itemRepository.countByTitleContainingIgnoreCase(query))
               .thenReturn(countMono);

       Mono<Page<Item>> resultMono = itemService.searchItems(query, 2, 5, null);

       StepVerifier.create(resultMono)
               .assertNext(page -> {
                   assertThat(page).isNotNull();
                   assertThat(page.getContent()).hasSize(1);
                   assertThat(page.getTotalElements()).isEqualTo(1L);
                   assertThat(page.getNumber()).isEqualTo(1); // page 2 (0-indexed)
                   assertThat(page.getSize()).isEqualTo(5);
               })
               .verifyComplete();

       verify(itemRepository).findByTitleContainingIgnoreCase(query, 5, 5);
       verify(itemRepository).countByTitleContainingIgnoreCase(query);
   }

   @Test
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
   void deleteItem_WithValidId_ShouldDeleteItem() {
       Long itemId = 1L;
       when(itemRepository.deleteById(itemId))
               .thenReturn(Mono.empty());

       Mono<Void> resultMono = itemService.deleteItem(itemId);

       StepVerifier.create(resultMono)
               .verifyComplete();

       verify(itemRepository).deleteById(itemId);
   }
} 