package io.clh.bookstore.categories;


import io.clh.models.Book;
import io.clh.models.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class CategoryService implements ICategory{
    private final SessionFactory sessionFactory;

    public CategoryService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public List<Book> GetAllBooksByCategory(Integer id) {
        return null;
    }

    @Override
    public Category AddBookToCategory(Integer bookId) {
        return null;
    }

    @Override
    public Category AddCategory(Category category) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(category);
            transaction.commit();

            return category;
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public Category DeleteCategory(Integer categoryId) {
        return null;
    }

    @Override
    public List<Category> GetAllCategories() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Category> cq = cb.createQuery(Category.class);
            Root<Category> rootEntry = cq.from(Category.class);
            CriteriaQuery<Category> all = cq.select(rootEntry);

            TypedQuery<Category> allQuery = session.createQuery(all);
            return allQuery.getResultList();
        }
    }

    @Override
    public Category UpdateCategory(Category category) {
        return null;
    }
}
