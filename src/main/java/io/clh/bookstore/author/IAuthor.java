package io.clh.bookstore.author;

import io.clh.models.Author;

import java.util.List;

// AuthorService
public interface IAuthor {
    Author addAuthor(Author author);

    Author getAuthorById(Integer id);

    List<Author> getAllAuthors(int page);

    Author setUrlAvatar(String url, Integer id) throws IllegalAccessException;
}