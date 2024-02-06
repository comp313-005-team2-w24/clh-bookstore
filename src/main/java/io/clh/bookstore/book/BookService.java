package io.clh.bookstore.book;

import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
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

    public Book getBookById(Integer bookId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.book_id = :bookId", Book.class)
                    .setParameter("bookId", bookId)
                    .uniqueResult();
        }
    }

    @Override
    @Transactional
    public List<Book> getAllBooks() {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT * FROM books " +
                    "FULL OUTER JOIN public.book_authors ba ON books.book_id = ba.book_id " +
                    "ORDER BY books.book_id LIMIT 10 OFFSET 0";
            List results = session.createNativeQuery(sql).list();
            return results;
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

    public Book getBookWithAuthors(Integer bookId) {
        Book book = null;
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.book_id = :bookId";
            Query<Book> query = session.createQuery(hql, Book.class);
            query.setParameter("bookId", bookId);
            book = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }
}
