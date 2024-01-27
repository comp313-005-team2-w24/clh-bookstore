package io.clh.bookstore.author;

import java.util.List;

public interface IAuthor {
    public void addAuthor(io.clh.models.Author author);
    public io.clh.models.Author getAuthorById(int id);
    List<io.clh.models.Author> getAllAuthors();
}