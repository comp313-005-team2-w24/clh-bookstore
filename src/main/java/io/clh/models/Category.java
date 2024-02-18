package io.clh.models;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@RequiredArgsConstructor()
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Table(name = "categories")
@ToString
public class Category {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany
    private Set<Book> books;
}
