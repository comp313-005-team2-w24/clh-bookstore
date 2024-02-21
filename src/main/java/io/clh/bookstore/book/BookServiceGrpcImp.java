package io.clh.bookstore.book;

import io.clh.bookstore.author.AuthorServiceImp;
import io.clh.bookstore.bookstore.Book;
import io.clh.bookstore.bookstore.BookServiceGrpc;
import io.clh.bookstore.entities.Entities;
import io.clh.bookstore.untils.GrpcEntitiesToModels;
import io.clh.bookstore.untils.ModelsToGrpcEntities;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static io.clh.bookstore.untils.ModelsToGrpcEntities.BookModelToGrpc;

@RequiredArgsConstructor
public class BookServiceGrpcImp extends BookServiceGrpc.BookServiceImplBase {

    private final BookService bookService;
    private final AuthorServiceImp authorServiceImp;

    @Override
    public void createBook(Book.CreateBookRequest request, StreamObserver<Book.CreateBookResponse> responseObserver) {
        try {
            GrpcEntitiesToModels converter = new GrpcEntitiesToModels();
            io.clh.models.Book book = converter.convertFromBookProto(request.getBook(), authorServiceImp);
            io.clh.models.Book createdBook = bookService.createBook(book);
            Entities.Book grpcBook = BookModelToGrpc(createdBook);
            Book.CreateBookResponse response = Book.CreateBookResponse.newBuilder().setBook(grpcBook).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getBookById(Book.GetBookByIdRequest request, StreamObserver<Book.GetBookByIdResponse> responseObserver) {
        super.getBookById(request, responseObserver);
    }

    @Override
    public void getAllBooks(Book.GetAllBooksRequest request, StreamObserver<Book.GetAllBooksResponse> responseObserver) {
        super.getAllBooks(request, responseObserver);
    }

    @Override
    public void updateBook(Book.UpdateBookRequest request, StreamObserver<Book.UpdateBookResponse> responseObserver) {
        super.updateBook(request, responseObserver);
    }

    @Override
    public void deleteBook(Book.DeleteBookRequest request, StreamObserver<Book.DeleteBookResponse> responseObserver) {
        super.deleteBook(request, responseObserver);
    }

    ///.........................................................................


    @Override
    public void getBookById(BookOuterClass.GetBookByIdRequest request, StreamObserver<BookOuterClass.GetBookByIdResponse> responseObserver) {
        try {
            io.clh.models.Book book = bookService.getBookById((int) request.getId());
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

            List<io.clh.models.Book> books = bookService.getAllBooks(limitPages);
            List<BookOuterClass.Book> responseBooks = books.stream().map(ModelsToGrpcEntities::convertToBookOuterBookProto).collect(Collectors.toList());
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
            ModelsToGrpcEntities modelsToGrpcEntities = new ModelsToGrpcEntities();
            io.clh.models.Book bookToUpdate = modelsToGrpcEntities.convertFromBookProto(request.getBook(), authorServiceImp);
            io.clh.models.Book updatedBook = bookService.updateBook(bookToUpdate);
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
            io.clh.models.Book book = bookService.deleteBookById((int) bookId);
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
