package io.clh.bookstore.book;

import io.clh.bookstore.BookOuterClass;
import io.clh.bookstore.BookServiceGrpc;
import io.clh.bookstore.author.AuthorService;
import io.clh.bookstore.untils.DtoProtoConversions;
import io.clh.models.Book;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.stream.Collectors;

import static io.clh.bookstore.untils.DtoProtoConversions.convertToBookOuterBookProto;

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
            DtoProtoConversions dtoProtoConversions = new DtoProtoConversions();
            Book book = dtoProtoConversions.convertFromBookProto(request.getBook(), authorService);

            Book createdBook = bookService.createBook(book);
            BookOuterClass.Book responseBook = convertToBookOuterBookProto(createdBook);
            BookOuterClass.CreateBookResponse response = BookOuterClass.CreateBookResponse.newBuilder().setBook(responseBook).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }


    @Override
    public void getBookById(BookOuterClass.GetBookByIdRequest request, StreamObserver<BookOuterClass.GetBookByIdResponse> responseObserver) {
        try {
            Book book = bookService.getBookById((int) request.getId());
            if (book != null) {
                BookOuterClass.Book responseBook = convertToBookOuterBookProto(book);
                BookOuterClass.GetBookByIdResponse response = BookOuterClass.GetBookByIdResponse.newBuilder().setBook(responseBook).build();
                responseObserver.onNext(response);
            } else {
                responseObserver.onError(new Throwable("Book not found with ID: " + request.getId()));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllBooks(BookOuterClass.GetAllBooksRequest request, StreamObserver<BookOuterClass.GetAllBooksResponse> responseObserver) {
        try {
            int limitPages = request.getPage() == 0 ? 1 : request.getPage();

            List<Book> books = bookService.getAllBooks(limitPages);
            List<BookOuterClass.Book> responseBooks = books.stream().map(DtoProtoConversions::convertToBookOuterBookProto).collect(Collectors.toList());
            BookOuterClass.GetAllBooksResponse response = BookOuterClass.GetAllBooksResponse.newBuilder().addAllBooks(responseBooks).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.UpdateBookResponse> responseObserver) {
        try {
            DtoProtoConversions dtoProtoConversions = new DtoProtoConversions();
            Book bookToUpdate = dtoProtoConversions.convertFromBookProto(request.getBook(), authorService);
            Book updatedBook = bookService.updateBook(bookToUpdate);
            BookOuterClass.Book responseBook = convertToBookOuterBookProto(updatedBook);
            BookOuterClass.UpdateBookResponse response = BookOuterClass.UpdateBookResponse.newBuilder().setBook(responseBook).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteBook(BookOuterClass.DeleteBookRequest request, StreamObserver<BookOuterClass.DeleteBookResponse> responseObserver) {
        try {
            long bookId = request.getBook().getBookId();
            Book book = bookService.deleteBookById((int) bookId);
            if (book != null) {
                BookOuterClass.Book responseBook = convertToBookOuterBookProto(book);
                BookOuterClass.DeleteBookResponse response = BookOuterClass.DeleteBookResponse.newBuilder().setBook(responseBook).build();
                responseObserver.onNext(response);
            } else {
                responseObserver.onError(new Throwable("Book not able to delete with ID: " + bookId));
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
