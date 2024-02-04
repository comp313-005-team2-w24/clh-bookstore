package io.clh.bookstore.author;

import io.clh.models.Author;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorServiceTest {
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

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();

        try (Connection conn = DriverManager.getConnection(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword()
        ); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE authors (" + "    author_id SERIAL PRIMARY KEY," + "    name VARCHAR(100)," + "    avatar_url VARCHAR(255)," + "    biography TEXT" + ");");
            //    stmt.execute("CREATE TABLE categories (" + "    category_id SERIAL PRIMARY KEY," + "    name VARCHAR(100)," + "    description TEXT" + ");");
            //    stmt.execute("CREATE TABLE books (" + "    book_id SERIAL PRIMARY KEY," + "    title VARCHAR(255)," + "    description TEXT," + "    isbn VARCHAR(20)," + "    publication_date DATE," + "    price DECIMAL(10, 2)," + "    stock_quantity INT," + "    author_id INT REFERENCES authors(author_id)," + "    category_id INT REFERENCES categories(category_id)" + ");");
            //    stmt.execute("CREATE TABLE orders (" + "    order_id SERIAL PRIMARY KEY," + "    book_id INT REFERENCES books(book_id)," + "    quantity INT," + "    order_date DATE," + "    total_price DECIMAL(10, 2)," + "    delivery_status VARCHAR(50)" + ");");
            //    stmt.execute("CREATE TABLE reviews (" + "    review_id SERIAL PRIMARY KEY," + "    book_id INT REFERENCES books(book_id)," + "    rating INT," + "    comment TEXT," + "    review_date DATE" + ");");
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
    public void createAuthor() {
        AuthorService authorService = new AuthorService(sessionFactory);
        Author author = new Author(1, "username".toCharArray(), "my biblio", "");

        session.beginTransaction();
        authorService.addAuthor(author);
        Author retrievedAuthor = authorService.getAuthorById(1);
        session.getTransaction().commit();

        Assertions.assertNotNull(retrievedAuthor);
        Assertions.assertEquals("username", new String(retrievedAuthor.getName()));
        assertEquals("my biblio", retrievedAuthor.getBiography());
    }

    @Test
    @Order(2)
    public void getAllAuthorsShouldNotBeEmpty() {
        AuthorService authorService = new AuthorService(sessionFactory);
        List<Author> authors = authorService.getAllAuthors();

        assertFalse(authors.isEmpty(), "The list of authors should not be empty");
    }


    @Test
    @Order(3)
    public void setAuthorImageUrlAvatar() throws IllegalAccessException {
        AuthorService authorService = new AuthorService(sessionFactory);
        Author author = authorService.setUrlAvatar("https://0.gravatar.com/avatar/1b4e9e532c9fbb9e7eec83c0a2cb8884bfb996017696c7a419c0ec92b870a35b?size=256", 1);

        Assertions.assertNotNull(author);
    }
}