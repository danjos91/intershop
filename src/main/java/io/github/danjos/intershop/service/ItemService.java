package io.github.danjos.intershop.service;

import io.github.danjos.intershop.exception.NotFoundException;
import io.github.danjos.intershop.model.Item;
import io.github.danjos.intershop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final CacheService cacheService;
    
    private static final String ITEM_CACHE_PREFIX = "item:";
    private static final String ITEMS_CACHE_PREFIX = "items:";
    private static final String SEARCH_CACHE_PREFIX = "search:";
    private static final Duration CACHE_TTL = Duration.ofHours(2);

    @Transactional(readOnly = true)
    public Mono<Page<Item>> searchItems(String query, int pageNumber, int pageSize, String sort) {
        String cacheKey = generateSearchCacheKey(query, pageNumber, pageSize, sort);
        
        return cacheService.get(cacheKey, Page.class)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for search, fetching from database");
                    return fetchItemsFromDatabase(query, pageNumber, pageSize, sort)
                            .flatMap(page -> cacheService.set(cacheKey, page, CACHE_TTL)
                                    .thenReturn(page));
                }))
                .map(page -> (Page<Item>) page);
    }
    
    private Mono<Page<Item>> fetchItemsFromDatabase(String query, int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        
        Flux<Item> itemsFlux;
        Mono<Long> totalCountMono;
        
        if (query != null && !query.isEmpty()) {
            itemsFlux = itemRepository.findByTitleOrDescriptionContainingIgnoreCase(query, limit, offset);
            totalCountMono = itemRepository.countByTitleOrDescriptionContainingIgnoreCase(query);
        } else if ("ALPHA".equals(sort)) {
            itemsFlux = itemRepository.findByOrderByTitleAsc(limit, offset);
            totalCountMono = itemRepository.countAll();
        } else if ("PRICE".equals(sort)) {
            itemsFlux = itemRepository.findByOrderByPriceAsc(limit, offset);
            totalCountMono = itemRepository.countAll();
        } else {
            itemsFlux = itemRepository.findAllByOrderByIdAsc(limit, offset);
            totalCountMono = itemRepository.countAll();
        }
        
        return Mono.zip(itemsFlux.collectList(), totalCountMono)
                .map(tuple -> {
                    var items = tuple.getT1();
                    var total = tuple.getT2();
                    return new PageImpl<>(items, pageable, total);
                });
    }
    
    private String generateSearchCacheKey(String query, int pageNumber, int pageSize, String sort) {
        return SEARCH_CACHE_PREFIX + 
               (query != null ? query.hashCode() : "null") + ":" + 
               pageNumber + ":" + 
               pageSize + ":" + 
               (sort != null ? sort : "default");
    }

    public Mono<Item> getItemById(Long id) {
        String cacheKey = ITEM_CACHE_PREFIX + id;
        
        return cacheService.get(cacheKey, Item.class)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for item {}, fetching from database", id);
                    return itemRepository.findById(id)
                            .switchIfEmpty(Mono.error(new NotFoundException("Item with id " + id + " not found")))
                            .flatMap(item -> cacheService.set(cacheKey, item, CACHE_TTL)
                                    .thenReturn(item));
                }));
    }

    public Flux<Item> getItemByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Flux.empty();
        }
        
        String cacheKey = ITEMS_CACHE_PREFIX + String.join(",", ids.stream().map(String::valueOf).toList());
        
        return cacheService.getList(cacheKey, Item.class)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for items {}, fetching from database", ids);
                    return itemRepository.findAllById(ids)
                            .collectList()
                            .flatMap(items -> {
                                if (items.isEmpty()) {
                                    return Mono.error(new NotFoundException("Items with ids: " + ids + " not found"));
                                }
                                return cacheService.setList(cacheKey, items, CACHE_TTL)
                                        .thenReturn(items);
                            });
                }))
                .flatMapMany(Flux::fromIterable);
    }

}