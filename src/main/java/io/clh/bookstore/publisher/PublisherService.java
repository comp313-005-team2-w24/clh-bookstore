package io.clh.bookstore.publisher;

import io.clh.models.Publisher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
public class PublisherService implements IPublisher {
    private final SessionFactory sessionFactory;

    public PublisherService(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Publisher addPublisher(Publisher publisher){
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.save(publisher);
            transaction.commit();

            return publisher;
        } catch(RuntimeException e){
            if (transaction != null) transaction.rollback();


            throw e;
        }

    }
    public Publisher getPublisherById(Integer id) {
         Session session = sessionFactory.openSession();
         Publisher publisher = session.get(Publisher.class, id);
         session.close();
         return publisher;

    }

    @Override
    public List<Publisher> getAllPublishers() {
        Session session = sessionFactory.openSession();
        List<Publisher> publishers = session.createQuery("from Publisher", Publisher.class).list();
        session.close();
        return publishers;
    }


}
