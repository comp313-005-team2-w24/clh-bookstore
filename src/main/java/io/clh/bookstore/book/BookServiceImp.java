package io.clh.bookstore.book;

import io.clh.bookstore.BookOuterClass;
import io.clh.bookstore.BookServiceGrpc;
import io.grpc.stub.StreamObserver;

public class BookServiceImp extends BookServiceGrpc.BookServiceImplBase {

    private final BookService bookService;

    public BookServiceImp(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void createBook(BookOuterClass.CreateBookRequest request, StreamObserver<BookOuterClass.CreateBookResponse> responseObserver) {
        super.createBook(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void getBookById(BookOuterClass.GetBookByIdRequest request, StreamObserver<BookOuterClass.GetBookByIdResponse> responseObserver) {
        super.getBookById(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void getAllBooks(BookOuterClass.GetAllBooksRequest request, StreamObserver<BookOuterClass.GetAllBooksResponse> responseObserver) {
        super.getAllBooks(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void updateBook(BookOuterClass.UpdateBookRequest request, StreamObserver<BookOuterClass.UpdateBookResponse> responseObserver) {
        super.updateBook(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void linkBookWithAuthors(BookOuterClass.LinkBookWithAuthorsRequest request, StreamObserver<BookOuterClass.LinkBookWithAuthorsResponse> responseObserver) {
        super.linkBookWithAuthors(request, responseObserver);
    }
}
