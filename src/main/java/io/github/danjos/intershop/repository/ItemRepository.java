package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Item> findByOrderByTitleAsc(Pageable pageable);
    Page<Item> findByOrderByPriceAsc(Pageable pageable);
}
