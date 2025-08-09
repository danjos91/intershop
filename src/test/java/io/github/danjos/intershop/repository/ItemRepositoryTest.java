package io.github.danjos.intershop.repository;


import io.github.danjos.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@ActiveProfiles("test")
@DisplayName("ItemRepository Tests")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item laptop;
    private Item smartphone;
    private Item tablet;

    @BeforeEach
    void setUp() {
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

    @Nested
    @DisplayName("Find By Title Containing Tests")
    class FindByTitleContainingTests {

        @Test
        @DisplayName("Should find items by title containing query")
        void findByTitleContainingIgnoreCase_WithQuery_ShouldReturnMatchingItems() {
            Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase("laptop", 10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> {
                        assertThat(item.getTitle()).isEqualTo("Laptop");
                        assertThat(item.getDescription()).isEqualTo("High performance laptop");
                    })
                    .verifyComplete();
        }


        @Test
        @DisplayName("Should find items with case insensitive search")
        void findByTitleContainingIgnoreCase_WithCaseInsensitiveQuery_ShouldReturnMatchingItems() {
            Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase("LAPTOP", 10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> {
                        assertThat(item.getTitle()).isEqualTo("Laptop");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should find items with partial query")
        void findByTitleContainingIgnoreCase_WithPartialQuery_ShouldReturnMatchingItems() {
            Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase("lap", 10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> {
                        assertThat(item.getTitle()).isEqualTo("Laptop");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return empty flux for non-matching query")
        void findByTitleContainingIgnoreCase_WithNonMatchingQuery_ShouldReturnEmptyFlux() {
            Flux<Item> result = itemRepository.findByTitleContainingIgnoreCase("nonexistent", 10, 0);

            StepVerifier.create(result)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle pagination correctly")
        void findByTitleContainingIgnoreCase_WithPagination_ShouldReturnCorrectItems() {
            // Add more items for pagination test
            for (int i = 0; i < 5; i++) {
                Item item = new Item();
                item.setTitle("Laptop " + i);
                item.setDescription("Description " + i);
                item.setPrice(100.0 + i);
                item.setStock(5);
                item.setImgPath("/images/laptop" + i + ".jpg");
                itemRepository.save(item).block();
            }

            Flux<Item> firstPage = itemRepository.findByTitleContainingIgnoreCase("laptop", 3, 0);
            Flux<Item> secondPage = itemRepository.findByTitleContainingIgnoreCase("laptop", 3, 3);

            StepVerifier.create(firstPage)
                    .expectNextCount(3)
                    .verifyComplete();

            StepVerifier.create(secondPage)
                    .expectNextCount(3)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Count By Title Containing Tests")
    class CountByTitleContainingTests {

        @Test
        @DisplayName("Should count items by title containing query")
        void countByTitleContainingIgnoreCase_WithQuery_ShouldReturnCorrectCount() {
            Mono<Long> result = itemRepository.countByTitleContainingIgnoreCase("laptop");

            StepVerifier.create(result)
                    .assertNext(count -> assertThat(count).isEqualTo(1L))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should count items with case insensitive search")
        void countByTitleContainingIgnoreCase_WithCaseInsensitiveQuery_ShouldReturnCorrectCount() {
            Mono<Long> result = itemRepository.countByTitleContainingIgnoreCase("LAPTOP");

            StepVerifier.create(result)
                    .assertNext(count -> assertThat(count).isEqualTo(1L))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return zero for non-matching query")
        void countByTitleContainingIgnoreCase_WithNonMatchingQuery_ShouldReturnZero() {
            Mono<Long> result = itemRepository.countByTitleContainingIgnoreCase("nonexistent");

            StepVerifier.create(result)
                    .assertNext(count -> assertThat(count).isEqualTo(0L))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Find By Title Or Description Containing Tests")
    class FindByTitleOrDescriptionContainingTests {

        @Test
        @DisplayName("Should find items by title or description containing query")
        void findByTitleOrDescriptionContainingIgnoreCase_WithQuery_ShouldReturnMatchingItems() {
            Flux<Item> result = itemRepository.findByTitleOrDescriptionContainingIgnoreCase("performance", 10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> {
                        assertThat(item.getTitle()).isEqualTo("Laptop");
                        assertThat(item.getDescription()).contains("performance");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should find items with case insensitive search")
        void findByTitleOrDescriptionContainingIgnoreCase_WithCaseInsensitiveQuery_ShouldReturnMatchingItems() {
            Flux<Item> result = itemRepository.findByTitleOrDescriptionContainingIgnoreCase("PERFORMANCE", 10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> {
                        assertThat(item.getTitle()).isEqualTo("Laptop");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return empty flux for non-matching query")
        void findByTitleOrDescriptionContainingIgnoreCase_WithNonMatchingQuery_ShouldReturnEmptyFlux() {
            Flux<Item> result = itemRepository.findByTitleOrDescriptionContainingIgnoreCase("nonexistent", 10, 0);

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Count By Title Or Description Containing Tests")
    class CountByTitleOrDescriptionContainingTests {

        @Test
        @DisplayName("Should count items by title or description containing query")
        void countByTitleOrDescriptionContainingIgnoreCase_WithQuery_ShouldReturnCorrectCount() {
            Mono<Long> result = itemRepository.countByTitleOrDescriptionContainingIgnoreCase("performance");

            StepVerifier.create(result)
                    .assertNext(count -> assertThat(count).isEqualTo(1L))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return zero for non-matching query")
        void countByTitleOrDescriptionContainingIgnoreCase_WithNonMatchingQuery_ShouldReturnZero() {
            Mono<Long> result = itemRepository.countByTitleOrDescriptionContainingIgnoreCase("nonexistent");

            StepVerifier.create(result)
                    .assertNext(count -> assertThat(count).isEqualTo(0L))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Find By Order By Title Asc Tests")
    class FindByOrderByTitleAscTests {

        @Test
        @DisplayName("Should return items ordered by title ascending")
        void findByOrderByTitleAsc_ShouldReturnOrderedItems() {
            Flux<Item> result = itemRepository.findByOrderByTitleAsc(10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Laptop"))
                    .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Smartphone"))
                    .assertNext(item -> assertThat(item.getTitle()).isEqualTo("Tablet"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle pagination correctly")
        void findByOrderByTitleAsc_WithPagination_ShouldReturnCorrectItems() {
            Flux<Item> result = itemRepository.findByOrderByTitleAsc(2, 0);

            StepVerifier.create(result)
                    .expectNextCount(2)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Find By Order By Price Asc Tests")
    class FindByOrderByPriceAscTests {

        @Test
        @DisplayName("Should return items ordered by price ascending")
        void findByOrderByPriceAsc_ShouldReturnOrderedItems() {
            Flux<Item> result = itemRepository.findByOrderByPriceAsc(10, 0);

            StepVerifier.create(result)
                    .assertNext(item -> assertThat(item.getPrice()).isEqualTo(399.99))
                    .assertNext(item -> assertThat(item.getPrice()).isEqualTo(599.99))
                    .assertNext(item -> assertThat(item.getPrice()).isEqualTo(999.99))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Find All By Order By Id Asc Tests")
    class FindAllByOrderByIdAscTests {

        @Test
        @DisplayName("Should return all items ordered by id ascending")
        void findAllByOrderByIdAsc_ShouldReturnOrderedItems() {
            Flux<Item> result = itemRepository.findAllByOrderByIdAsc(10, 0);

            StepVerifier.create(result)
                    .expectNextCount(3)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Count All Tests")
    class CountAllTests {

        @Test
        @DisplayName("Should return total count of all items")
        void countAll_ShouldReturnTotalCount() {
            Mono<Long> result = itemRepository.countAll();

            StepVerifier.create(result)
                    .assertNext(count -> assertThat(count).isEqualTo(3L))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find item by valid id")
        void findById_WithValidId_ShouldReturnItem() {
            Item savedItem = itemRepository.save(laptop).block();

            Mono<Item> result = itemRepository.findById(savedItem.getId());

            StepVerifier.create(result)
                    .assertNext(item -> {
                        assertThat(item.getId()).isEqualTo(savedItem.getId());
                        assertThat(item.getTitle()).isEqualTo("Laptop");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return empty for non-existent id")
        void findById_WithNonExistentId_ShouldReturnEmpty() {
            Mono<Item> result = itemRepository.findById(999L);

            StepVerifier.create(result)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Save Tests")
    class SaveTests {

        @Test
        @DisplayName("Should save new item")
        void save_NewItem_ShouldSaveItem() {
            Item newItem = new Item();
            newItem.setTitle("New Item");
            newItem.setDescription("New description");
            newItem.setPrice(100.0);
            newItem.setStock(5);
            newItem.setImgPath("/images/new.jpg");

            Mono<Item> result = itemRepository.save(newItem);

            StepVerifier.create(result)
                    .assertNext(savedItem -> {
                        assertThat(savedItem.getId()).isNotNull();
                        assertThat(savedItem.getTitle()).isEqualTo("New Item");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should update existing item")
        void save_ExistingItem_ShouldUpdateItem() {
            Item savedItem = itemRepository.save(laptop).block();
            savedItem.setTitle("Updated Laptop");

            Mono<Item> result = itemRepository.save(savedItem);

            StepVerifier.create(result)
                    .assertNext(updatedItem -> {
                        assertThat(updatedItem.getId()).isEqualTo(savedItem.getId());
                        assertThat(updatedItem.getTitle()).isEqualTo("Updated Laptop");
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete item by id")
        void deleteById_WithValidId_ShouldDeleteItem() {
            Item savedItem = itemRepository.save(laptop).block();
            Long itemId = savedItem.getId();

            Mono<Void> deleteResult = itemRepository.deleteById(itemId);

            StepVerifier.create(deleteResult)
                    .verifyComplete();

            Mono<Item> foundItem = itemRepository.findById(itemId);
            StepVerifier.create(foundItem)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle deleting non-existent item")
        void deleteById_WithNonExistentId_ShouldCompleteSuccessfully() {
            Mono<Void> deleteResult = itemRepository.deleteById(999L);

            StepVerifier.create(deleteResult)
                    .verifyComplete();
        }
    }
}
