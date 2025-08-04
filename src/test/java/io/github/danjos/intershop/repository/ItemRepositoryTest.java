package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
class ItemRepositoryTest {

   @Autowired
   private ItemRepository itemRepository;

   private Item laptop;
   private Item smartphone;
   private Item tablet;

   @BeforeEach
   void setUp() {
       // Initialize test data
       laptop = new Item();
       laptop.setTitle("Laptop");
       laptop.setDescription("High performance laptop");
       laptop.setPrice(999.99);
       laptop.setStock(10);
       laptop.setImgPath("/images/laptop.jpg");

       smartphone = new Item();
       smartphone.setTitle("Smartphone");
       smartphone.setDescription("Latest smartphone model");
       smartphone.setPrice(599.99);
       smartphone.setStock(15);
       smartphone.setImgPath("/images/smartphone.jpg");

       tablet = new Item();
       tablet.setTitle("Tablet");
       tablet.setDescription("Portable tablet device");
       tablet.setPrice(399.99);
       tablet.setStock(8);
       tablet.setImgPath("/images/tablet.jpg");

       // Clear and save test data
       itemRepository.deleteAll()
               .thenMany(itemRepository.saveAll(List.of(laptop, smartphone, tablet)))
               .blockLast();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithValidQuery_ShouldReturnMatchingItems() {
       String query = "laptop";
       Pageable pageable = PageRequest.of(0, 10);

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

       StepVerifier.create(result)
               .assertNext(item -> {
                   assertThat(item.getTitle()).isEqualTo("Laptop");
               })
               .verifyComplete();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithCaseInsensitiveQuery_ShouldReturnMatchingItems() {
       String query = "LAPTOP";
       Pageable pageable = PageRequest.of(0, 10);

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

       StepVerifier.create(result)
               .assertNext(item -> {
                   assertThat(item.getTitle()).isEqualTo("Laptop");
               })
               .verifyComplete();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithPartialQuery_ShouldReturnMatchingItems() {
       String query = "phone";
       Pageable pageable = PageRequest.of(0, 10);

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

       StepVerifier.create(result)
               .assertNext(item -> {
                   assertThat(item.getTitle()).isEqualTo("Smartphone");
               })
               .verifyComplete();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithNonExistentQuery_ShouldReturnEmptyFlux() {
       String query = "nonexistent";
       Pageable pageable = PageRequest.of(0, 10);

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

       StepVerifier.create(result)
               .verifyComplete();
   }

   @Test
   void findByOrderByTitleAsc_ShouldReturnItemsSortedAlphabetically() {
       Pageable pageable = PageRequest.of(0, 10);

       Flux<Item> result = itemRepository.findByOrderByTitleAsc(pageable);

       StepVerifier.create(result)
               .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Laptop"))
               .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Smartphone"))
               .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Tablet"))
               .verifyComplete();
   }

   @Test
   void findByOrderByPriceAsc_ShouldReturnItemsSortedByPrice() {
       Pageable pageable = PageRequest.of(0, 10);

       Flux<Item> result = itemRepository.findByOrderByPriceAsc(pageable);

       StepVerifier.create(result)
               .assertNext(item -> assertThat(item.getPrice()).isEqualTo(399.99))
               .assertNext(item -> assertThat(item.getPrice()).isEqualTo(599.99))
               .assertNext(item -> assertThat(item.getPrice()).isEqualTo(999.99))
               .verifyComplete();
   }

   @Test
   void save_WithValidItem_ShouldPersistItem() {
       Item newItem = new Item();
       newItem.setTitle("Headphones");
       newItem.setDescription("Wireless headphones");
       newItem.setPrice(199.99);
       newItem.setStock(20);
       newItem.setImgPath("/images/headphones.jpg");

       Mono<Item> savedItemMono = itemRepository.save(newItem);

       StepVerifier.create(savedItemMono)
               .assertNext(savedItem -> {
                   assertThat(savedItem).isNotNull();
                   assertThat(savedItem.getId()).isNotNull();
                   assertThat(savedItem.getTitle()).isEqualTo("Headphones");
               })
               .verifyComplete();
   }

   @Test
   void findById_WithValidId_ShouldReturnItem() {
       Item savedItem = itemRepository.save(laptop).block();

       Mono<Item> foundItemMono = itemRepository.findById(savedItem.getId());

       StepVerifier.create(foundItemMono)
               .assertNext(foundItem -> {
                   assertThat(foundItem).isNotNull();
                   assertThat(foundItem.getTitle()).isEqualTo("Laptop");
                   assertThat(foundItem.getPrice()).isEqualTo(999.99);
               })
               .verifyComplete();
   }

   @Test
   void findById_WithInvalidId_ShouldReturnEmpty() {
       Mono<Item> result = itemRepository.findById(999L);

       StepVerifier.create(result)
               .verifyComplete();
   }

   @Test
   void deleteById_WithValidId_ShouldRemoveItem() {
       Item savedItem = itemRepository.save(laptop).block();
       Long itemId = savedItem.getId();

       Mono<Void> deleteResult = itemRepository.deleteById(itemId);

       StepVerifier.create(deleteResult)
               .verifyComplete();

       Mono<Item> foundItem = itemRepository.findById(itemId);
       StepVerifier.create(foundItem)
               .verifyComplete();
   }
} 