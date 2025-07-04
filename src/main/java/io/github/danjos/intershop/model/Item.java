package io.github.danjos.intershop.model;

import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Item {

    private long id;
    private String name;
    private String description;
}
