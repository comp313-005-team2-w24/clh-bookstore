package io.clh.bookstore.book;

import io.clh.bookstore.author.AuthorService;
import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.*;

public class BookService implements IBook {
    private final SessionFactory sessionFactory;
    private final AuthorService authorService;

    public BookService(SessionFactory sessionFactory, AuthorService authorService) {
        this.sessionFactory = sessionFactory;

        this.authorService = authorService;
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
    public List<Book> getAllBooks(int page) {
        final int pageSize = 50; // Number of books per page

        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Book> cq = cb.createQuery(Book.class);
            Root<Book> bookRoot = cq.from(Book.class);
            bookRoot.fetch("authors", JoinType.LEFT);
            cq.select(bookRoot).distinct(true).orderBy(cb.asc(bookRoot.get("book_id")));

            int firstResult = (page - 1) * pageSize;

            TypedQuery<Book> query = session.createQuery(cq)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize);

            return query.getResultList();
        }
    }


    @Override
    public Set<Book> findBooksByAuthorId(int authorId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Book> cq = cb.createQuery(Book.class);
            Root<Author> author = cq.from(Author.class);
            Join<Author, Book> books = author.join("books", JoinType.INNER);
            cq.select(books).where(cb.equal(author.get("author_id"), authorId));

            return new HashSet<>(session.createQuery(cq).getResultList());
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
    public Book getBookById(int bookId) {
        try (Session session = sessionFactory.openSession()) {
            String jpql = "SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.id = :bookId";
            return session.createQuery(jpql, Book.class)
                    .setParameter("bookId", bookId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
