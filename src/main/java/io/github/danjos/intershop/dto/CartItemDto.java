package io.github.danjos.intershop.dto;

import io.github.danjos.intershop.model.Item;
import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private String title;
    private String description;
    private double price;
    private String imgPath;
    private int count;  // <- This is the quantity in cart

    public CartItemDto(Item item, int count) {
        this.id = item.getId();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.imgPath = item.getImgPath();
        this.count = count;
    }

}