package io.clh.bookstore.publisher;

import io.clh.bookstore.publisher.PublisherService;
import io.clh.models.Publisher;
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
public class PublisherServiceImpTest {
    private static SessionFactory sessionFactory;
    private static Session session;

    @Container
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>("postgres:latest").withDatabaseName("testdb").withUsername("test").withPassword("test");

    @BeforeAll
    public static void setUp(){
        postgresqlContainer.start();

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", postgresqlContainer.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgresqlContainer.getUsername());
        configuration.setProperty("hibernate.connection.password", postgresqlContainer.getPassword());
        configuration.addAnnotatedClass(Publisher.class);

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();

        try (Connection conn = DriverManager.getConnection(
                postgresqlContainer.getJdbcUrl(),
                postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword()
        ); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE publishers (" + "    publisher_id SERIAL PRIMARY KEY," + "    name VARCHAR(100)," + "    location VARCHAR(255)" + ");");
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
    public void createPublisher() {
        PublisherService publisherService = new PublisherService(sessionFactory);
        Publisher publisher = new Publisher(1, "publisherName", "publisherLocation");

        session.beginTransaction();
        publisherService.addPublisher(publisher);
        Publisher retrievedPublisher = publisherService.getPublisherById(1);
        session.getTransaction().commit();

        Assertions.assertNotNull(retrievedPublisher);
        Assertions.assertEquals("publisherName", retrievedPublisher.getName());
        // Add additional assertions if needed
    }

    @Test
    @Order(2)
    public void getAllPublisherShouldNotBeEmpty() {
        PublisherService publisherService = new PublisherService(sessionFactory);
        List<Publisher> publishers = publisherService.getAllPublishers();

        assertFalse(publishers.isEmpty(), "The list of authors should not be empty");
    }


}
