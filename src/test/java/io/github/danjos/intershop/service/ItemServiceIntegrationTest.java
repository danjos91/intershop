package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
   void searchItems_WithQuery_ShouldReturnFilteredResults() {
       Page<Item> result = itemService.searchItems("laptop", 1, 10, null);

       assertThat(result).isNotNull();
       assertThat(result.getContent()).hasSize(1);
       assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
   }

   @Test
   void searchItems_WithCaseInsensitiveQuery_ShouldReturnResults() {
       Page<Item> result = itemService.searchItems("LAPTOP", 1, 10, null);

       assertThat(result).isNotNull();
       assertThat(result.getContent()).hasSize(1);
       assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
   }

   @Test
   void searchItems_WithPartialQuery_ShouldReturnResults() {
       Page<Item> result = itemService.searchItems("phone", 1, 10, null);

       assertThat(result).isNotNull();
       assertThat(result.getContent()).hasSize(1);
       assertThat(result.getContent().get(0).getTitle()).isEqualTo("Smartphone");
   }

   @Test
   void searchItems_WithAlphaSort_ShouldReturnSortedResults() {
       Page<Item> result = itemService.searchItems(null, 1, 10, "ALPHA");

       assertThat(result).isNotNull();
       assertThat(result.getContent()).hasSize(3);
       assertThat(result.getContent().get(0).getTitle()).isEqualTo("Laptop");
       assertThat(result.getContent().get(1).getTitle()).isEqualTo("Smartphone");
       assertThat(result.getContent().get(2).getTitle()).isEqualTo("Tablet");
   }

   @Test
   void searchItems_WithPriceSort_ShouldReturnSortedResults() {
       Page<Item> result = itemService.searchItems(null, 1, 10, "PRICE");

       assertThat(result).isNotNull();
       assertThat(result.getContent()).hasSize(3);
       assertThat(result.getContent().get(0).getPrice()).isEqualTo(399.99);
       assertThat(result.getContent().get(1).getPrice()).isEqualTo(599.99);
       assertThat(result.getContent().get(2).getPrice()).isEqualTo(999.99);
   }

   @Test
   void searchItems_WithoutQueryAndSort_ShouldReturnAllItems() {
       Page<Item> result = itemService.searchItems(null, 1, 10, null);

       assertThat(result).isNotNull();
       assertThat(result.getContent()).hasSize(3);
   }

   @Test
   void getItemById_WithValidId_ShouldReturnItem() {
       Item savedItem = itemRepository.save(laptop);

       Item result = itemService.getItemById(savedItem.getId());

       assertThat(result).isNotNull();
       assertThat(result.getId()).isEqualTo(savedItem.getId());
       assertThat(result.getTitle()).isEqualTo("Laptop");
   }

   @Test
   void getItemById_WithInvalidId_ShouldThrowException() {
       org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
           itemService.getItemById(999L);
       });
   }

   @Test
   void deleteItem_WithValidId_ShouldDeleteItem() {
       Item savedItem = itemRepository.save(laptop);
       Long itemId = savedItem.getId();

       itemService.deleteItem(itemId);

       var result = itemRepository.findById(itemId);
       assertThat(result).isEmpty();
   }

   @Test
   void searchItems_WithPagination_ShouldReturnCorrectPage() {
       for (int i = 0; i < 15; i++) {
           Item item = new Item();
           item.setTitle("Item " + i);
           item.setDescription("Description " + i);
           item.setPrice(100.0 + i);
           item.setStock(5);
           itemRepository.save(item);
       }

       Page<Item> firstPage = itemService.searchItems(null, 1, 10, null);
       Page<Item> secondPage = itemService.searchItems(null, 2, 10, null);

       assertThat(firstPage.getContent()).hasSize(10);
       assertThat(firstPage.getTotalElements()).isEqualTo(18);
       assertThat(firstPage.getTotalPages()).isEqualTo(2);
       assertThat(secondPage.getContent()).hasSize(8);
   }
}
