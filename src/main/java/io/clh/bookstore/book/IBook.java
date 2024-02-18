package io.clh.bookstore.book;

import io.clh.models.Book;

import java.util.List;
import java.util.Set;

public interface IBook {
    Book createBook(Book book);
    List<Book> getAllBooks(int page);
    Book updateBook(Book book);

    Book getBookById(int bookId);
    Set<Book> findBooksByAuthorId(int authorId);

    Book deleteBookById(int bookId);
}
