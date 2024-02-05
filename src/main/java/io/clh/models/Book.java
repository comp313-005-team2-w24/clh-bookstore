package io.clh.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@RequiredArgsConstructor()
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer book_id;

    private String title;
    private String description;
    private String isbn;
    private java.sql.Date publicationDate;
    private Double price;
    private Integer stockQuantity;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;
}
