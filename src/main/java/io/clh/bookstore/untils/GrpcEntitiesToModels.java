package io.clh.bookstore.untils;

import com.google.protobuf.Timestamp;
import io.clh.bookstore.author.AuthorService;
import io.clh.bookstore.entities.Entities;
import io.clh.models.Author;
import io.clh.models.Book;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

public class GrpcEntitiesToModels {
    public static Author AuthorGrpcToAuthorModel(Entities.AuthorEntity authorEntity) {
        return Author.builder()
                .avatar_url(authorEntity.getAvatarUrl())
                .biography(authorEntity.getBiography())
                .name(authorEntity.getName().toCharArray())
                .build();
    }

    public Book convertFromBookProto(Entities.Book bookProto, AuthorService authorService) {
        Date date = null;

        if (bookProto.hasPublicationDate()) {
            Timestamp ts = bookProto.getPublicationDate();
            long millis = ts.getSeconds() * 1000 + ts.getNanos() / 1000000;
            date = new Date(millis);
        }

        Set<Author> authors = new HashSet<>();
        for (long authorId : bookProto.getAuthorIdsList()) {
            Author author = authorService.getAuthorById(authorId);
            if (author != null) {
                authors.add(author);
            }
        }

        return Book.builder()
                .book_id(bookProto.getBookId())
                .title(bookProto.getTitle())
                .description(bookProto.getDescription())
                .isbn(bookProto.getIsbn())
                .price(bookProto.getPrice())
                .stockQuantity(bookProto.getStockQuantity())
                .avatar_url(bookProto.getAvatarUrl())
                .publicationDate(date)
                .authors(authors)
                .build();
    }
}
