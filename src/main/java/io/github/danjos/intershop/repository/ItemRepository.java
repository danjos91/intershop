package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    @Query("SELECT * FROM items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Flux<Item> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT * FROM items ORDER BY title ASC")
    Flux<Item> findByOrderByTitleAsc(Pageable pageable);

    @Query("SELECT * FROM items ORDER BY price ASC")
    Flux<Item> findByOrderByPriceAsc(Pageable pageable);
}