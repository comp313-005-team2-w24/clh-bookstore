package io.clh.bookstore.book;

import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Date;
import java.util.*;

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
    public List<Book> getAllBooks() {
        try (Session session = sessionFactory.openSession()) {
            String sql = "SELECT books.book_id AS bookId, books.title, books.description, books.isbn, books.publication_date AS publicationDate, books.price, books.stock_quantity AS stockQuantity, " +
                    "ba.author_id AS authorId " +
                    "FROM books " +
                    "FULL OUTER JOIN public.book_authors ba ON books.book_id = ba.book_id " +
                    "ORDER BY books.book_id LIMIT 10 OFFSET 0";

            List<Object[]> results = session.createNativeQuery(sql)
                    .addScalar("bookId", StandardBasicTypes.LONG) // Use appropriate types
                    .addScalar("title", StandardBasicTypes.STRING)
                    .addScalar("description", StandardBasicTypes.STRING)
                    .addScalar("isbn", StandardBasicTypes.STRING)
                    .addScalar("publicationDate", StandardBasicTypes.DATE)
                    .addScalar("price", StandardBasicTypes.DOUBLE)
                    .addScalar("stockQuantity", StandardBasicTypes.INTEGER)
                    .addScalar("authorId", StandardBasicTypes.LONG)
                    .getResultList();

            Map<Integer, Book> booksMap = new HashMap<>();

            for (Object[] row : results) {
                Integer bookId = ((Long) row[0]).intValue();
                Book book = booksMap.get(bookId);

                if (book == null) {
                    book = new Book();
                    book.setBook_id(bookId);
                    book.setTitle((String) row[1]);
                    book.setDescription((String) row[2]);
                    book.setIsbn((String) row[3]);
                    book.setPublicationDate((Date) row[4]);
                    book.setPrice(Double.valueOf(row[5].toString()));
                    book.setStockQuantity((Integer) row[6]);
                    book.setAuthors(new HashSet<>());
                    booksMap.put(bookId, book);
                }

                Long authorId = (Long) row[7];
                if (authorId != null) {
                    Author author = new Author();
                    author.setAuthor_id(authorId.intValue());
                    book.getAuthors().add(author);
                }
            }

            return new ArrayList<>(booksMap.values());
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
            String sql = "SELECT books.book_id AS bookId, books.title, books.description, books.isbn, books.publication_date AS publicationDate, books.price, books.stock_quantity AS stockQuantity, " +
                    "ba.author_id AS authorId " +
                    "FROM books " +
                    "LEFT JOIN public.book_authors ba ON books.book_id = ba.book_id " +
                    "WHERE books.book_id = :bookId";

            List<Object[]> results = session.createNativeQuery(sql)
                    .setParameter("bookId", bookId)
                    .addScalar("bookId", StandardBasicTypes.LONG) // Use appropriate types
                    .addScalar("title", StandardBasicTypes.STRING)
                    .addScalar("description", StandardBasicTypes.STRING)
                    .addScalar("isbn", StandardBasicTypes.STRING)
                    .addScalar("publicationDate", StandardBasicTypes.DATE)
                    .addScalar("price", StandardBasicTypes.DOUBLE)
                    .addScalar("stockQuantity", StandardBasicTypes.INTEGER)
                    .addScalar("authorId", StandardBasicTypes.LONG)
                    .getResultList();

            Book book = null;

            for (Object[] row : results) {
                if (book == null) {
                    book = new Book();
                    book.setBook_id(((Long) row[0]).intValue());
                    book.setTitle((String) row[1]);
                    book.setDescription((String) row[2]);
                    book.setIsbn((String) row[3]);
                    book.setPublicationDate((Date) row[4]);
                    book.setPrice(Double.valueOf(row[5].toString()));
                    book.setStockQuantity((Integer) row[6]);
                    book.setAuthors(new HashSet<>());
                }

                Long authorId = (Long) row[7];
                if (authorId != null) {
                    Author author = new Author();
                    author.setAuthor_id(authorId.intValue());
                    book.getAuthors().add(author);
                }
            }

            return book;
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
