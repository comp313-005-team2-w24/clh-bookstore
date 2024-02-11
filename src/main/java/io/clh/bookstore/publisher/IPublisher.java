package io.clh.bookstore.publisher;

import io.clh.models.Author;
import io.clh.models.Publisher;

import java.util.List;

public interface IPublisher {
    Publisher addPublisher(Publisher publisher);

    Publisher getPublisherById(Integer id);

    List<Publisher> getAllPublishers();

}
