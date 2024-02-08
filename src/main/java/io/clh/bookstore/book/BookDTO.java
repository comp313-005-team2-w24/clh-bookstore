package io.clh.bookstore.book;

import io.clh.models.Author;

import java.sql.Date;
import java.util.Set;

public class BookDTO {
    private Integer book_id;

    private String title;
    private String description;
    private String isbn;

    private java.sql.Date publicationDate;

    private Double price;
    private Integer stockQuantity;
    private Long authorId;

    public BookDTO(Integer book_id, String title, String description, String isbn, Date publicationDate, Double price, Integer stockQuantity, Long authorId) {
        this.book_id = book_id;
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.authorId = authorId;
    }
}
