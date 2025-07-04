package io.github.danjos.intershop.model;

import lombok.Data;

import java.util.List;

@Data
public class Order {

    public long id;
    public List<Item> items;

}
