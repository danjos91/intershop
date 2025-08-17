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
import org.springframework.data.redis.core.ReactiveRedisTemplate;
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
    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    
    private static final String ITEM_CACHE_PREFIX = "item:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(1);

    @Transactional(readOnly = true)
    public Mono<Page<Item>> searchItems(String query, int pageNumber, int pageSize, String sort) {
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

    public Mono<Item> getItemById(Long id) {
        String cacheKey = ITEM_CACHE_PREFIX + id;
        
        return redisTemplate.opsForValue().get(cacheKey)
                .map(cachedItem -> {
                    log.info("Cache hit for item: {}", id);
                    return (Item) cachedItem;
                })
                .switchIfEmpty(
                    itemRepository.findById(id)
                        .flatMap(item -> {
                            log.info("Cache miss for item: {}, storing in cache", id);
                            return redisTemplate.opsForValue()
                                    .set(cacheKey, item, CACHE_TTL)
                                    .thenReturn(item);
                        })
                        .switchIfEmpty(Mono.error(new NotFoundException("Item with id " + id + " not found")))
                );
    }

    public Flux<Item> getItemByIds(Set<Long> ids) {
        return Flux.fromIterable(ids)
                .flatMap(this::getItemById);
    }
    
    public Mono<Void> clearItemCache(Long itemId) {
        String cacheKey = ITEM_CACHE_PREFIX + itemId;
        log.info("Clearing cache for item: {}", itemId);
        return redisTemplate.delete(cacheKey).then();
    }
    
    public Mono<Void> clearAllItemCache() {
        log.info("Clearing all item cache");
        return redisTemplate.keys(ITEM_CACHE_PREFIX + "*")
                .flatMap(redisTemplate::delete)
                .then();
    }

}