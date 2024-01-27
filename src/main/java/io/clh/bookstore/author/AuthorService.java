package io.clh.bookstore.author;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class AuthorService implements IAuthor{

    // TODO: remove all try catch to level up.
    private final SessionFactory sessionFactory;

    public AuthorService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void addAuthor(io.clh.models.Author author) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.save(author);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    @Override
    public io.clh.models.Author getAuthorById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(io.clh.models.Author.class, id);
        }
    }

    @Override
    public List<io.clh.models.Author> getAllAuthors() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Author", io.clh.models.Author.class).list();
        }
    }
}
