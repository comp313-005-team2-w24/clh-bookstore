package io.clh.bookstore.author;

import java.util.List;
import io.clh.models.Author;

public interface IAuthor {
    public Author addAuthor(Author author);
    public Author getAuthorById(Integer id);
    List<Author> getAllAuthors();
}