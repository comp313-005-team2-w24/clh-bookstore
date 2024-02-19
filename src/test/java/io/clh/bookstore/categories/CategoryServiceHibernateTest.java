package io.clh.bookstore.categories;

import io.clh.models.Author;
import io.clh.models.Book;
import io.clh.models.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryServiceHibernateTest {
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
        configuration.addAnnotatedClass(Category.class);

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
                    "    avatar_url VARCHAR(255)," +
                    "    category_id INT" +
                    ");");

            stmt.execute("CREATE TABLE book_authors (" +
                    "    book_id INT NOT NULL," +
                    "    author_id INT NOT NULL," +
                    "    PRIMARY KEY (book_id, author_id)," +
                    "    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE," +
                    "    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE" +
                    ");");


            stmt.execute("create table categories\n" +
                    "(\n" +
                    "    id          bigint not null\n" +
                    "        primary key,\n" +
                    "    description varchar(255),\n" +
                    "    name        varchar(255)\n" +
                    ");");

            stmt.execute("create table categories_books\n" +
                    "(\n" +
                    "    category_id   bigint  not null\n" +
                    "        constraint fk3i5qlw63appsdgy6qtp0pqk83\n" +
                    "            references categories,\n" +
                    "    books_book_id integer not null\n" +
                    "        constraint uk_rtsclxyko9ppqks6acta4i84t\n" +
                    "            unique\n" +
                    "        constraint fkbdd1ei67142eh1gh84dhhvw17\n" +
                    "            references books,\n" +
                    "    primary key (category_id, books_book_id)\n" +
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
    public void CreateCategory() {
        CategoryService categoryService = new CategoryService(sessionFactory);

        Set<Book> emptyBooks = Set.of();

        Category category = new Category();
        category.setId(1L);
        category.setName("Novel");
        category.setDescription("Test");
        category.setBooks(emptyBooks);

        Transaction tx = session.beginTransaction();
        Category addedCategory = categoryService.AddCategory(category);
        tx.commit();

        Assertions.assertEquals(addedCategory.getId(), 1);
    }

    @Test
    @Order(2)
    public void GetAllCategories() {
        CategoryService categoryService = new CategoryService(sessionFactory);

        Transaction tx = session.beginTransaction();
        List<Category> categories = categoryService.GetAllCategories();
        tx.commit();

        Optional<Long> allCategories = Optional.ofNullable(categories.get(0).getId());

        Assertions.assertFalse(categories.isEmpty());
        Assertions.assertEquals(allCategories.get(), 1);
    }

}