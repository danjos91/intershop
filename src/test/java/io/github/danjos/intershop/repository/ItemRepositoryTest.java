package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.TestDatabaseConfig;
import io.github.danjos.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
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
@Import(TestDatabaseConfig.class)
class ItemRepositoryTest {

   @Autowired
   private ItemRepository itemRepository;

   private Item laptop;
   private Item smartphone;
   private Item tablet;
   private int limit;
   private int offset;

   @BeforeEach
   void setUp() {
       limit = 10;
       offset = 0;

       itemRepository.deleteAll();

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

       itemRepository.saveAll(List.of(laptop, smartphone, tablet));
   }

   @Test
   void findByTitleContainingIgnoreCase_WithValidQuery_ShouldReturnMatchingItems() {
       String query = "laptop";

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, limit, offset);

       StepVerifier.create(result)
               .assertNext(item -> {
                   assertThat(item.getTitle()).isEqualTo("Test Laptop");
               })
               .verifyComplete();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithCaseInsensitiveQuery_ShouldReturnMatchingItems() {
       String query = "LAPTOP";

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, limit, offset);

       StepVerifier.create(result)
               .assertNext(item -> {
                   assertThat(item.getTitle()).isEqualTo("Test Laptop");
               })
               .verifyComplete();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithPartialQuery_ShouldReturnMatchingItems() {
       String query = "phone";

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, limit, offset);

       StepVerifier.create(result)
               .assertNext(item -> {
                   assertThat(item.getTitle()).isEqualTo("Test Smartphone");
               })
               .verifyComplete();
   }

   @Test
   void findByTitleContainingIgnoreCase_WithNonExistentQuery_ShouldReturnEmptyFlux() {
       String query = "nonexistent";

       Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, limit, offset);

       StepVerifier.create(result)
               .verifyComplete();
   }

   @Test
   void findByOrderByTitleAsc_ShouldReturnItemsSortedAlphabetically() {
       Flux<Item> result = itemRepository.findByOrderByTitleAsc(limit, offset);

       StepVerifier.create(result)
               .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Test Laptop"))
               .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Test Smartphone"))
               .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Test Tablet"))
               .verifyComplete();
   }

   @Test
   void findByOrderByPriceAsc_ShouldReturnItemsSortedByPrice() {
       Flux<Item> result = itemRepository.findByOrderByPriceAsc(limit, offset);

       StepVerifier.create(result)
               .assertNext(item -> assertThat(item.getPrice()).isEqualTo(399.99))
               .assertNext(item -> assertThat(item.getPrice()).isEqualTo(599.99))
               .assertNext(item -> assertThat(item.getPrice()).isEqualTo(999.99))
               .verifyComplete();
   }

   @Test
   void save_WithValidItem_ShouldPersistItem() {
       Item newItem = new Item();
       newItem.setTitle("Test Headphones");
       newItem.setDescription("Wireless headphones for testing");
       newItem.setPrice(199.99);
       newItem.setStock(20);
       newItem.setImgPath("/images/headphones.jpg");

       Mono<Item> savedItemMono = itemRepository.save(newItem);

       StepVerifier.create(savedItemMono)
               .assertNext(savedItem -> {
                   assertThat(savedItem).isNotNull();
                   assertThat(savedItem.getId()).isNotNull();
                   assertThat(savedItem.getTitle()).isEqualTo("Test Headphones");
               })
               .verifyComplete();
   }

   @Test
   void findById_WithValidId_ShouldReturnItem() {
       // Use the test data that's already in the database
       Mono<Item> foundItemMono = itemRepository.findById(100L);

       StepVerifier.create(foundItemMono)
               .assertNext(foundItem -> {
                   assertThat(foundItem).isNotNull();
                   assertThat(foundItem.getTitle()).isEqualTo("Test Laptop");
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
       // First save a new item to delete
       Item itemToDelete = new Item();
       itemToDelete.setTitle("Item to Delete");
       itemToDelete.setDescription("This item will be deleted");
       itemToDelete.setPrice(100.00);
       itemToDelete.setStock(5);
       itemToDelete.setImgPath("/images/test.jpg");

       Item savedItem = itemRepository.save(itemToDelete).block();
       Long itemId = savedItem.getId();

       Mono<Void> deleteResult = itemRepository.deleteById(itemId);

       StepVerifier.create(deleteResult)
               .verifyComplete();

       Mono<Item> foundItem = itemRepository.findById(itemId);
       StepVerifier.create(foundItem)
               .verifyComplete();
   }
} 