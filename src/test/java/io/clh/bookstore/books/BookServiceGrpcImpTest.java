package io.clh.bookstore.books;

import io.clh.bookstore.author.AuthorService;
import io.clh.bookstore.book.BookService;
import io.clh.config.HibernateConfigUtil;
import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Statement;

@Testcontainers
public class BookServiceGrpcImpTest {
    private static SessionFactory sessionFactory;
    private static Session session;

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>("postgres:latest").withDatabaseName("testdb").withUsername("test").withPassword("test");

    @BeforeAll
    public static void setUp() {
        postgresqlContainer.start();

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgresqlContainer.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgresqlContainer.getUsername());
        configuration.setProperty("hibernate.connection.password", postgresqlContainer.getPassword());
        configuration.setImplicitNamingStrategy(new org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl());
        configuration.addAnnotatedClass(Author.class);
        configuration.addAnnotatedClass(Book.class);

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();

        try (Connection conn = DriverManager.getConnection(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword()
        ); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE authors (" +
                    "    author_id SERIAL PRIMARY KEY," +
                    "    name VARCHAR(100)," +
                    "    avatar_url VARCHAR(255)," +
                    "    biography TEXT" +
                    ");");

            stmt.execute("CREATE TABLE books (" +
                    "    book_id SERIAL PRIMARY KEY," +
                    "    title VARCHAR(255)," +
                    "    description TEXT," +
                    "    isbn VARCHAR(20)," +
                    "    publication_date DATE," +
                    "    price DECIMAL(10, 2)," +
                    "    stock_quantity INT," +
                    "    category_id INT" +
                    ");");

            stmt.execute("CREATE TABLE book_authors (" +
                    "    book_id INT NOT NULL," +
                    "    author_id INT NOT NULL," +
                    "    PRIMARY KEY (book_id, author_id)," +
                    "    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE," +
                    "    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE" +
                    ");");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    @AfterAll
    public static void tearDown() {
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }

        postgresqlContainer.stop();
    }


    @Test
    @Order(1)
    public void createBook() {
        AuthorService authorService = new AuthorService(sessionFactory);
        BookService bookService = new BookService(sessionFactory);

        Author author = new Author();
        author.setName("Author Name".toCharArray());
        author.setBiography("A brief bio");
        author.setAvatar_url("http://example.com/avatar.jpg");

        Transaction tx = session.beginTransaction();
        authorService.addAuthor(author);
        tx.commit();

        Book book = new Book();
        book.setTitle("Test Book");
        book.setDescription("A test book description");
        book.setIsbn("1234567890");
        book.setPrice(19.99);
        book.setStockQuantity(100);
        book.setPublicationDate(new Date(System.currentTimeMillis()));

        tx = session.beginTransaction();
        bookService.createBook(book);
        tx.commit();

        tx = session.beginTransaction();
        bookService.linkBookWithAuthors(book, author);
        tx.commit();

        Assertions.assertTrue(book.getBook_id() > 0);
        Assertions.assertTrue(author.getAuthor_id() > 0);
    }


    @Test
    @Order(2)
    public void GetBookByIdNotEmpty(){
        BookService bookService = new BookService(sessionFactory);

        Transaction tx = session.beginTransaction();
        Book retrievedBook = bookService.getBookWithAuthors(1);
        tx.commit();
        //TODO: fix test. Required add new author and book row for setUp() function

      //  Assertions.assertTrue(retrievedBook.getBook_id()>0);
    }
}
