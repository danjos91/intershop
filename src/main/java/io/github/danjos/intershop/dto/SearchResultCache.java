package io.github.danjos.intershop.dto;

import io.github.danjos.intershop.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultCache {
    private List<Item> items;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    
    public static SearchResultCache fromPage(Page<Item> page) {
        return new SearchResultCache(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }
    
    public Page<Item> toPage() {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(pageNumber, pageSize);
        return new PageImpl<>(items, pageable, totalElements);
    }
}
