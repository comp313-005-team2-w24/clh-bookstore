package io.clh.bookstore.author;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

import io.clh.models.Author;

public class AuthorService implements IAuthor{

    private final SessionFactory sessionFactory;

    public AuthorService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Author addAuthor(Author author) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(author);
        transaction.commit();
        session.close();
        return author;
    }

    @Override
    public Author getAuthorById(int id) {
        Session session = sessionFactory.openSession();
        Author author = session.get(Author.class, id);
        session.close();
        return author;
    }

    @Override
    public List<Author> getAllAuthors() {
        Session session = sessionFactory.openSession();
        List<Author> authors = session.createQuery("from Author", Author.class).list();
        session.close();
        return authors;
    }
}
