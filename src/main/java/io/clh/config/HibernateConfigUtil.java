package io.clh.config;

import io.clh.models.Author;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Properties;

public class HibernateConfigUtil {
    public static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration();

        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "org.postgresql.Driver");

        // TODO: string env with default value
        settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/your_db_name");
        settings.put(Environment.USER, "your_username");
        settings.put(Environment.PASS, "your_password");
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        settings.put(Environment.SHOW_SQL, "true");
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, "update");

        configuration.setProperties(settings);

        // Add annotated classes
        configuration.addAnnotatedClass(io.clh.models.Author.class);

        return configuration.buildSessionFactory();
    }
}
