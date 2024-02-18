package io.clh.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@RequiredArgsConstructor()
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Table(name = "authors")
@ToString(exclude = "books") // it causes issues with lazy loading
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int author_id;

    private char[] name;
    private String biography;
    private String avatar_url;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Book> books;
}
