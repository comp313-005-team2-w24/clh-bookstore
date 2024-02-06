package io.clh.bookstore.book;

import com.google.protobuf.Timestamp;
import io.clh.bookstore.BookOuterClass;
import io.clh.bookstore.BookServiceGrpc;
import io.clh.bookstore.author.AuthorService;
import io.clh.models.Author;
import io.clh.models.Book;
import io.grpc.stub.StreamObserver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookServiceGrpcImp extends BookServiceGrpc.BookServiceImplBase {

    private final BookService bookService;
    private final AuthorService authorService;

    public BookServiceGrpcImp(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @Override
    public void createBook(BookOuterClass.CreateBookRequest request, StreamObserver<BookOuterClass.CreateBookResponse> responseObserver) {
        try {
            Book book = this.convertFromBookProto(request.getBook());

            Book createdBook = bookService.createBook(book);
            BookOuterClass.Book responseBook = convertToBookProto(createdBook);
            BookOuterClass.CreateBookResponse response = BookOuterClass.CreateBookResponse.newBuilder().setBook(responseBook).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }


    @Override
    public void getBookById(BookOuterClass.GetBookByIdRequest request, StreamObserver<BookOuterClass.GetBookByIdResponse> responseObserver) {
        try {
            Book book = bookService.getBookById((int) request.getId());
            if (book != null) {
                BookOuterClass.Book responseBook = convertToBookProto(book);
                BookOuterClass.GetBookByIdResponse response = BookOuterClass.GetBookByIdResponse.newBuilder().setBook(responseBook).build();
                responseObserver.onNext(response);
            } else {
                responseObserver.onError(new Throwable("Book not found with ID: " + request.getId()));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllBooks(BookOuterClass.GetAllBooksRequest request, StreamObserver<BookOuterClass.GetAllBooksResponse> responseObserver) {
        try {
            List<Book> books = bookService.getAllBooks();
            List<BookOuterClass.Book> responseBooks = books.stream().map(this::convertToBookProto).collect(Collectors.toList());
            BookOuterClass.GetAllBooksResponse response = BookOuterClass.GetAllBooksResponse.newBuilder().addAllBooks(responseBooks).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.UpdateBookResponse> responseObserver) {
        try {
            Book bookToUpdate = convertFromBookProto(request.getBook());
            Book updatedBook = bookService.updateBook(bookToUpdate);
            BookOuterClass.Book responseBook = convertToBookProto(updatedBook);
            BookOuterClass.UpdateBookResponse response = BookOuterClass.UpdateBookResponse.newBuilder().setBook(responseBook).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void linkBookWithAuthors(BookOuterClass.LinkBookWithAuthorsRequest request, StreamObserver<BookOuterClass.LinkBookWithAuthorsResponse> responseObserver) {
        try {
            Book book = bookService.getBookById((int) request.getBookId());
            //TODO: Fix
            Book updatedBook = bookService.linkBookWithAuthors(book,
                    request.getAuthorIdsList()
                            .stream()
                            .map(id -> new Author()).distinct().toArray(Author[]::new)
            );

            BookOuterClass.Book responseBook = convertToBookProto(updatedBook);
            BookOuterClass.LinkBookWithAuthorsResponse response = BookOuterClass.LinkBookWithAuthorsResponse.newBuilder().setBook(responseBook).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }


    private BookOuterClass.Book convertToBookProto(Book book) {
        Timestamp timestamp = null;
        if (book.getPublicationDate() != null) {
            long millis = book.getPublicationDate().getTime();
            timestamp = Timestamp.newBuilder()
                    .setSeconds(millis / 1000)
                    .setNanos((int) ((millis % 1000) * 1000000))
                    .build();
        }

        BookOuterClass.Book.Builder bookBuilder = BookOuterClass.Book.newBuilder()
                .setBookId(book.getBook_id())
                .setPrice(book.getPrice())
                .setIsbn(book.getIsbn())
                .setDescription(book.getDescription())
                .setTitle(book.getTitle())
                .setStockQuantity(book.getStockQuantity());

        if (timestamp != null) {
            bookBuilder.setPublicationDate(timestamp);
        }

        // Convert Set<Author> to a list of author IDs and add it to the book builder
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            List<Long> authorIds = book.getAuthors().stream()
                    .map(Author::getAuthor_id)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            bookBuilder.addAllAuthorIds(authorIds);
        }

        return bookBuilder.build();
    }


    private Book convertFromBookProto(BookOuterClass.Book bookProto) {
        Book book = new Book();
        book.setBook_id((int) bookProto.getBookId());
        book.setTitle(bookProto.getTitle());
        book.setDescription(bookProto.getDescription());
        book.setIsbn(bookProto.getIsbn());
        book.setPrice(bookProto.getPrice());
        book.setStockQuantity(bookProto.getStockQuantity());

        if (bookProto.hasPublicationDate()) {
            Timestamp ts = bookProto.getPublicationDate();
            long millis = ts.getSeconds() * 1000 + ts.getNanos() / 1000000;
            book.setPublicationDate(new java.sql.Date(millis));
        }

        Set<Author> authors = new HashSet<>();
        for (long authorId : bookProto.getAuthorIdsList()) {
            // todo: fix
            Author author = authorService.getAuthorById((int) authorId);
            if (author != null) {
                authors.add(author);
            }
        }
        book.setAuthors(authors);

        return book;
    }
}
