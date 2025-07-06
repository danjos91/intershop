package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void deleteItem(Long id) {
    }

    public void createItem(String title, String text, MultipartFile image, String tags) {
    }

    public Item getItemById(Long id) {
        //TODO check teory
        return null;
    }

    public Page<Item> getItems(String search, int pageNumber, int pageSize) {
        return null;
    }
}
