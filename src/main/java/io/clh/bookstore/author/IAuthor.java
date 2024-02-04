package io.clh.bookstore.author;

import io.clh.models.Author;

import java.util.List;

public interface IAuthor {
    Author addAuthor(Author author);

    Author getAuthorById(Integer id);

    List<Author> getAllAuthors();

    Author setUrlAvatar(String url, Integer id) throws IllegalAccessException;
}