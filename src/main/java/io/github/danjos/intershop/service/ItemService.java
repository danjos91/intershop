package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public List<Item> searchItems(String query, String sort) {
        if (query != null && !query.isEmpty()) {
            return itemRepository.findByTitleContaining(query);
        }
        if ("ALPHA".equals(sort)) {
            return itemRepository.findByOrderByTitleAsc();
        }
        if ("PRICE".equals(sort)) {
            return itemRepository.findByOrderByPriceAsc();
        }
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow();
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}





