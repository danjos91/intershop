package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("ItemService Integration Tests")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

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

    @Nested
    @DisplayName("Search Items Integration Tests")
    class SearchItemsIntegrationTests {

        @Test
        @DisplayName("Should return filtered results when query is provided")
        void searchItems_WithQuery_ShouldReturnFilteredResults() {
            Mono<Page<Item>> resultMono = itemService.searchItems("laptop", 1, 10, null);

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getContent()).hasSize(1);
                        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
                        assertThat(result.getTotalElements()).isEqualTo(1L);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return paginated results when no query and sort specified")
        void searchItems_WithoutQueryAndSort_ShouldReturnPaginatedResults() {
            Mono<Page<Item>> resultMono = itemService.searchItems(null, 1, 10, null);

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getContent()).hasSize(3);
                        assertThat(result.getTotalElements()).isEqualTo(3L);
                        assertThat(result.getTotalPages()).isEqualTo(1);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return sorted results when ALPHA sort is specified")
        void searchItems_WithAlphaSort_ShouldReturnSortedResults() {
            Mono<Page<Item>> resultMono = itemService.searchItems(null, 1, 10, "ALPHA");

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getContent()).hasSize(3);
                        assertThat(result.getTotalElements()).isEqualTo(3L);
                        // Check if items are sorted alphabetically
                        List<Item> items = result.getContent();
                        assertThat(items.get(0).getTitle()).isEqualTo("Laptop");
                        assertThat(items.get(1).getTitle()).isEqualTo("Smartphone");
                        assertThat(items.get(2).getTitle()).isEqualTo("Tablet");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return sorted results when PRICE sort is specified")
        void searchItems_WithPriceSort_ShouldReturnSortedResults() {
            Mono<Page<Item>> resultMono = itemService.searchItems(null, 1, 10, "PRICE");

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getContent()).hasSize(3);
                        assertThat(result.getTotalElements()).isEqualTo(3L);
                        // Check if items are sorted by price
                        List<Item> items = result.getContent();
                        assertThat(items.get(0).getPrice()).isLessThanOrEqualTo(items.get(1).getPrice());
                        assertThat(items.get(1).getPrice()).isLessThanOrEqualTo(items.get(2).getPrice());
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return results for partial query")
        void searchItems_WithPartialQuery_ShouldReturnResults() {
            Mono<Page<Item>> resultMono = itemService.searchItems("lap", 1, 10, null);

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getContent()).hasSize(1);
                        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
                        assertThat(result.getTotalElements()).isEqualTo(1L);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return results for case insensitive query")
        void searchItems_WithCaseInsensitiveQuery_ShouldReturnResults() {
            Mono<Page<Item>> resultMono = itemService.searchItems("LAPTOP", 1, 10, null);

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getContent()).hasSize(1);
                        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
                        assertThat(result.getTotalElements()).isEqualTo(1L);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle pagination correctly")
        void searchItems_WithPagination_ShouldReturnCorrectPage() {
            // Clear existing data and add more items for pagination test
            itemRepository.deleteAll().block();
            
            for (int i = 0; i < 15; i++) {
                Item item = new Item();
                item.setTitle("Item " + i);
                item.setDescription("Description " + i);
                item.setPrice(100.0 + i);
                item.setStock(5);
                item.setImgPath("/images/item" + i + ".jpg");
                itemRepository.save(item).block();
            }

            Mono<Page<Item>> firstPageMono = itemService.searchItems(null, 1, 10, null);
            Mono<Page<Item>> secondPageMono = itemService.searchItems(null, 2, 10, null);

            StepVerifier.create(firstPageMono)
                    .assertNext(firstPage -> {
                        assertThat(firstPage.getContent()).hasSize(10);
                        assertThat(firstPage.getTotalElements()).isEqualTo(15);
                        assertThat(firstPage.getTotalPages()).isEqualTo(2);
                        assertThat(firstPage.getNumber()).isEqualTo(0);
                    })
                    .verifyComplete();

            StepVerifier.create(secondPageMono)
                    .assertNext(secondPage -> {
                        assertThat(secondPage.getContent()).hasSize(5);
                        assertThat(secondPage.getTotalElements()).isEqualTo(15);
                        assertThat(secondPage.getTotalPages()).isEqualTo(2);
                        assertThat(secondPage.getNumber()).isEqualTo(1);
                    })
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Get Item By ID Integration Tests")
    class GetItemByIdIntegrationTests {

        @Test
        @DisplayName("Should return item when valid ID is provided")
        void getItemById_WithValidId_ShouldReturnItem() {
            Item savedItem = itemRepository.save(laptop).block();

            Mono<Item> resultMono = itemService.getItemById(savedItem.getId());

            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.getId()).isEqualTo(savedItem.getId());
                        assertThat(result.getTitle()).isEqualTo("Laptop");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw exception when invalid ID is provided")
        void getItemById_WithInvalidId_ShouldThrowException() {
            Mono<Item> resultMono = itemService.getItemById(999L);

            StepVerifier.create(resultMono)
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }
}
