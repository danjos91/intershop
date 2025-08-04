package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    @Query("SELECT * FROM items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%')) LIMIT :limit OFFSET :offset")
    Flux<Item> findByTitleContainingIgnoreCase(String title, int limit, int offset);

    @Query("SELECT COUNT(*) FROM items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Mono<Long> countByTitleContainingIgnoreCase(String title);

    @Query("SELECT * FROM items ORDER BY title ASC LIMIT :limit OFFSET :offset")
    Flux<Item> findByOrderByTitleAsc(int limit, int offset);

    @Query("SELECT * FROM items ORDER BY price ASC LIMIT :limit OFFSET :offset")
    Flux<Item> findByOrderByPriceAsc(int limit, int offset);

    @Query("SELECT COUNT(*) FROM items")
    Mono<Long> countAll();
}