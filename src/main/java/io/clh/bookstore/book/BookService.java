package io.clh.bookstore.book;

import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookService implements IBook {
    private final SessionFactory sessionFactory;

    public BookService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Book createBook(Book book) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(book);
            transaction.commit();
            return book;
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public Book getBookById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Book.class, id);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public List<Book> getAllBooks() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book", Book.class).list();
        }
    }

    @Override
    public Book updateBook(Book book) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Book linkBookWithAuthors(Book book, Author... authors) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Book persistedBook = session.get(Book.class, book.getBook_id());
            if (persistedBook != null) {
                Set<Author> authorSet = new HashSet<>(Arrays.asList(authors));
                persistedBook.setAuthors(authorSet);
                session.update(persistedBook);
                transaction.commit();
                return persistedBook;
            } else {
                throw new IllegalArgumentException("Book not found with provided ID");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
