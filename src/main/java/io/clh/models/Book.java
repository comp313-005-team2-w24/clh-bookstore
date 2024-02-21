package io.clh.models;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@NoArgsConstructor
@Table(name = "books")
@ToString(exclude = {"authors", "category"})
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //TODO: Long
    private Long book_id;

    private String title;
    private String description;
    private String isbn;

    @Column(name = "publication_date")
    private Date publicationDate;

    private Double price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "avatar_url")
    private String avatar_url;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return book_id != null && book_id.equals(book.book_id);
    }

    @Override
    public int hashCode() {
        return book_id == null ? 0 : book_id.hashCode();
    }
}
