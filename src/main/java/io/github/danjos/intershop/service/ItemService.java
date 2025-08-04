package io.github.danjos.intershop.service;

import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public Mono<Page<Item>> searchItems(String query, int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        
        Flux<Item> itemsFlux;
        if (query != null && !query.isEmpty()) {
            itemsFlux = itemRepository.findByTitleContainingIgnoreCase(query, pageable);
        } else if ("ALPHA".equals(sort)) {
            itemsFlux = itemRepository.findByOrderByTitleAsc(pageable);
        } else if ("PRICE".equals(sort)) {
            itemsFlux = itemRepository.findByOrderByPriceAsc(pageable);
        } else {
            itemsFlux = itemRepository.findAll(Sort.by("id"));
        }
        
        return itemsFlux.collectList()
                .map(items -> new PageImpl<>(items, pageable, items.size()));
    }

    public Mono<Item> getItemById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Item with id " + id + " not found")));
    }

    public Mono<Void> deleteItem(Long id) {
        return itemRepository.deleteById(id);
    }
}