package io.clh.bookstore.book;

import io.clh.models.Author;
import io.clh.models.Book;

import java.util.List;
import java.util.Set;

public interface IBook {
    Book createBook(Book book);
    List<Book> getAllBooks();
    Book updateBook(Book book);

    Book getBookById(int bookId);

//    Book linkBookWithAuthors(Long bookId, Set<Long> authorIds);

    Set<Book> findBooksByAuthorId(int authorId);
}
