package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item laptop;
    private Item smartphone;
    private Item tablet;

    @BeforeEach
    void setUp() {
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
        // Given
        String query = "laptop";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
    }

    @Test
    void findByTitleContainingIgnoreCase_WithCaseInsensitiveQuery_ShouldReturnMatchingItems() {
        // Given
        String query = "LAPTOP";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
    }

    @Test
    void findByTitleContainingIgnoreCase_WithPartialQuery_ShouldReturnMatchingItems() {
        // Given
        String query = "phone";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Smartphone");
    }

    @Test
    void findByTitleContainingIgnoreCase_WithNonExistentQuery_ShouldReturnEmptyPage() {
        // Given
        String query = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByOrderByTitleAsc_ShouldReturnItemsSortedAlphabetically() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Item> result = itemRepository.findByOrderByTitleAsc(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("Smartphone");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("Tablet");
    }

    @Test
    void findByOrderByPriceAsc_ShouldReturnItemsSortedByPrice() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Item> result = itemRepository.findByOrderByPriceAsc(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getPrice()).isEqualTo(399.99); // Tablet
        assertThat(result.getContent().get(1).getPrice()).isEqualTo(599.99); // Smartphone
        assertThat(result.getContent().get(2).getPrice()).isEqualTo(999.99); // Laptop
    }

    @Test
    void save_WithValidItem_ShouldPersistItem() {
        // Given
        Item newItem = new Item();
        newItem.setTitle("Headphones");
        newItem.setDescription("Wireless headphones");
        newItem.setPrice(199.99);
        newItem.setStock(20);
        newItem.setImgPath("/images/headphones.jpg");

        // When
        Item savedItem = itemRepository.save(newItem);

        // Then
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getTitle()).isEqualTo("Headphones");

        // Verify it's actually persisted
        Item foundItem = itemRepository.findById(savedItem.getId()).orElse(null);
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getTitle()).isEqualTo("Headphones");
    }

    @Test
    void findById_WithValidId_ShouldReturnItem() {
        // Given
        Item savedItem = itemRepository.save(laptop);

        // When
        Item foundItem = itemRepository.findById(savedItem.getId()).orElse(null);

        // Then
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getTitle()).isEqualTo("Laptop");
        assertThat(foundItem.getPrice()).isEqualTo(999.99);
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmpty() {
        // When
        var result = itemRepository.findById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_WithValidId_ShouldRemoveItem() {
        // Given
        Item savedItem = itemRepository.save(laptop);
        Long itemId = savedItem.getId();

        // When
        itemRepository.deleteById(itemId);

        // Then
        var result = itemRepository.findById(itemId);
        assertThat(result).isEmpty();
    }
} 