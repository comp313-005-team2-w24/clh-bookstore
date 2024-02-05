package io.clh.bookstore.book;

import io.clh.models.Author;
import io.clh.models.Book;

import java.util.List;

public interface IBook {
    Book createBook(Book book);
    Book getBookById(Long id);
    List<Book> getAllBooks();
    Book updateBook(Book book);
    Book linkBookWithAuthors(Book book, Author ... authors);
}
