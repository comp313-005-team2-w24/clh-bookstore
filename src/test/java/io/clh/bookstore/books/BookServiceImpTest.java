package io.clh.bookstore.books;

import io.clh.bookstore.book.BookService;
import io.clh.models.Author;
import io.clh.models.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Testcontainers
public class BookServiceImpTest {
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
    public void createBook(){
        BookService bookService = new BookService(sessionFactory);

    }

}
