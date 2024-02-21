package io.clh.bookstore.untils;

import com.google.protobuf.Timestamp;
import io.clh.bookstore.BookOuterClass;
import io.clh.bookstore.author.AuthorService;
import io.clh.bookstore.category.CategoryOuterClass;
import io.clh.models.Author;
import io.clh.models.Book;
import io.clh.models.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DtoProtoConversions {

    public static BookOuterClass.Book convertToBookOuterBookProto(Book book) {
        Timestamp timestamp = null;
        if (book.getPublicationDate() != null) {
            long millis = book.getPublicationDate().getTime();
            timestamp = Timestamp.newBuilder().setSeconds(millis / 1000).setNanos((int) ((millis % 1000) * 1000000)).build();
        }

        BookOuterClass.Book.Builder bookBuilder = BookOuterClass.Book.newBuilder().setBookId(book.getBook_id()).setPrice(book.getPrice()).setIsbn(book.getIsbn()).setDescription(book.getDescription()).setTitle(book.getTitle()).setStockQuantity(book.getStockQuantity());

        if (timestamp != null) {
            bookBuilder.setPublicationDate(timestamp);
        }

        if (book.getAvatar_url() != null) {
            bookBuilder.setAvatarUrl(book.getAvatar_url());
        }

        // Convert Set<Author> to a list of author IDs and add it to the book builder
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            List<Long> authorIds = book.getAuthors().stream().map(Author::getAuthor_id).map(Long::valueOf).collect(Collectors.toList());

            bookBuilder.addAllAuthorIds(authorIds);
        }

        return bookBuilder.build();
    }

    public static CategoryOuterClass.Book convertToCategoryOuterClassBookProto(Book book) {
        Timestamp timestamp = null;
        if (book.getPublicationDate() != null) {
            long millis = book.getPublicationDate().getTime();
            timestamp = Timestamp.newBuilder().setSeconds(millis / 1000).setNanos((int) ((millis % 1000) * 1000000)).build();
        }

        CategoryOuterClass.Book.Builder bookBuilder = CategoryOuterClass.Book.newBuilder().setBookId(book.getBook_id()).setPrice(book.getPrice()).setIsbn(book.getIsbn()).setDescription(book.getDescription()).setTitle(book.getTitle()).setStockQuantity(book.getStockQuantity());

        if (timestamp != null) {
            bookBuilder.setPublicationDate(timestamp);
        }

        if (book.getAvatar_url() != null) {
            bookBuilder.setAvatarUrl(book.getAvatar_url());
        }

        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            List<Long> authorIds = book.getAuthors().stream().map(Author::getAuthor_id).map(Long::valueOf).collect(Collectors.toList());

            bookBuilder.addAllAuthorIds(authorIds);
        }

        return bookBuilder.build();
    }

    public static io.clh.bookstore.author.Book convertToBookStoreAuthorBookProto(Book book) {
        Timestamp timestamp = null;
        if (book.getPublicationDate() != null) {
            long millis = book.getPublicationDate().getTime();
            timestamp = Timestamp.newBuilder().setSeconds(millis / 1000).setNanos((int) ((millis % 1000) * 1000000)).build();
        }


        io.clh.bookstore.author.Book.Builder bookBuilder = io.clh.bookstore.author.Book.newBuilder().setBookId(book.getBook_id()).setPrice(book.getPrice()).setIsbn(book.getIsbn()).setDescription(book.getDescription()).setTitle(book.getTitle()).setStockQuantity(book.getStockQuantity());


        if (book.getAvatar_url() != null) {
            bookBuilder.setAvatarUrl(book.getAvatar_url());
        }

        if (timestamp != null) {
            bookBuilder.setPublicationDate(timestamp);
        }

        // Convert Set<Author> to a list of author IDs and add it to the book builder
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            List<Long> authorIds = book.getAuthors().stream().map(Author::getAuthor_id).map(Long::valueOf).collect(Collectors.toList());

            bookBuilder.addAllAuthorIds(authorIds);
        }

        return bookBuilder.build();
    }

    public Book convertFromBookProto(BookOuterClass.Book bookProto, AuthorService authorService) {
        // TODO: Do the same for the others entities
        Book book = Book.builder().book_id((int) bookProto.getBookId()).title(bookProto.getTitle()).title(bookProto.getTitle()).description(bookProto.getDescription()).isbn(bookProto.getIsbn()).price(bookProto.getPrice()).stockQuantity(bookProto.getStockQuantity()).avatar_url(bookProto.getAvatarUrl()).build();

        if (bookProto.hasPublicationDate()) {
            Timestamp ts = bookProto.getPublicationDate();
            long millis = ts.getSeconds() * 1000 + ts.getNanos() / 1000000;
            book.setPublicationDate(new java.sql.Date(millis));
        }

        Set<Author> authors = new HashSet<>();
        for (long authorId : bookProto.getAuthorIdsList()) {
            Author author = authorService.getAuthorById((int) authorId);
            if (author != null) {
                authors.add(author);
            }
        }
        book.setAuthors(authors);

        return book;
    }

//    public static CategoryOuterClass.Category

    public static CategoryOuterClass.Category CategoryModelToCategoryGrpc(Category category) {
        CategoryOuterClass.Category.Builder builder = CategoryOuterClass.Category.newBuilder();

        builder.setId(category.getId());
        builder.setName(category.getName());
        builder.setDescription(category.getDescription());
        List<CategoryOuterClass.Book> list = category.getBooks().stream().map(DtoProtoConversions::convertToCategoryOuterClassBookProto).toList();
        builder.addAllBooks(list);

        return builder.build();
    }
}
