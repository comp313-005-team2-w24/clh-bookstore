package io.clh.bookstore.book;

import io.clh.bookstore.author.AuthorService;
import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;

import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

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


    public Book linkBookWithAuthors(Long bookId, Set<Long> authorIds) {
        //todo: do manually as an example above
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Fetch the book
            Book book = session.get(Book.class, bookId.intValue());
            if (book == null) {
                throw new IllegalArgumentException("Book not found with provided ID: " + bookId);
            }

            Author authorById = authorService.getAuthorById(1);
            Set<Author> authors = authorIds.stream()
                    .map(id -> authorService.getAuthorById(id.intValue()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (authors.isEmpty()) {
                throw new IllegalArgumentException("No valid authors found for provided IDs");
            }

            book.setAuthors(authors);
            session.saveOrUpdate(book);
            transaction.commit();

            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

}
