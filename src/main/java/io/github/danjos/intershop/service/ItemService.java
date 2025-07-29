package io.github.danjos.intershop.service;

import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public Page<Item> searchItems(String query, int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        if (query != null && !query.isEmpty()) {
            return itemRepository.findByTitleContainingIgnoreCase(query, pageable);
        }
        if ("ALPHA".equals(sort)) {
            return itemRepository.findByOrderByTitleAsc(pageable);
        }
        if ("PRICE".equals(sort)) {
            return itemRepository.findByOrderByPriceAsc(pageable);
        }
        return itemRepository.findAll(pageable);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow();
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}