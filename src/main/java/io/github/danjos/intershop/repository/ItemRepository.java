package io.github.danjos.intershop.repository;

import io.github.danjos.intershop.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByNameContaining(String name);
    List<Item> findByDescriptionContaining(String description);
}
