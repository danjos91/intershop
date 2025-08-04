package io.github.danjos.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Table("users")
@Data
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
    private String email;

    private transient List<Order> orders = new ArrayList<>();
}