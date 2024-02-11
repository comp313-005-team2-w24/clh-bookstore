package io.clh.bookstore.author;

import io.clh.bookstore.book.BookService;
import io.clh.bookstore.untils.DtoProtoConversions;
import io.clh.bookstore.untils.Metrics;
import io.clh.models.Author;
import io.clh.models.Book;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.prometheus.client.Histogram;

import java.util.Arrays;
import java.util.List;

public class AuthorServiceGrpcImp extends AuthorServiceGrpc.AuthorServiceImplBase {
    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorServiceGrpcImp(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @Override
    public void createAuthor(CreateAuthorRequest request, StreamObserver<AuthorEntity> responseObserver) {
        /*
         * Pay attention to package name
         * io.clh.models.Author is entity, while
         * io.clh.bookstore.author.Author is from gRPC
         */

        try {
            Metrics.incrementTotalRequests();
            Histogram.Timer requestTimer = Metrics.startRequestTimer();

            io.clh.models.Author author = new Author();
            author.setName(request.getName().toCharArray());
            author.setBiography(request.getBiography());
            author.setAvatar_url(request.getAvatarUrl());
            author.setBooks(null);

            Author createdAuthor = authorService.addAuthor(author);
            AuthorEntity response = AuthorEntity.newBuilder()
                    .setAuthorId(createdAuthor.getAuthor_id())
                    .setName(Arrays.toString(createdAuthor.getName()))
                    .setBiography(createdAuthor.getBiography())
                    .setAvatarUrl(createdAuthor.getAvatar_url())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            requestTimer.observeDuration();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllAuthors(GetAllAuthorsRequest request, StreamObserver<AuthorEntity> responseObserver) {
        try {
            Metrics.incrementTotalRequests();
            Histogram.Timer requestTimer = Metrics.startRequestTimer();

            int limitPages = request.getPage() == 0 ? 1 : request.getPage();
            List<Author> authors = authorService.getAllAuthors(limitPages);

            for (Author author : authors) {
                AuthorEntity response = AuthorEntity.newBuilder()
                        .setAuthorId(author.getAuthor_id())
                        .setName(new String(author.getName()))
                        .setBiography(author.getBiography())
                        .setAvatarUrl(author.getAvatar_url())
                        .build();

                responseObserver.onNext(response);

                requestTimer.observeDuration();
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAuthorById(AuthorByIdRequest request, StreamObserver<GetAuthorByIdResponse> responseObserver) {
        try {
            Metrics.incrementTotalRequests();
            Histogram.Timer requestTimer = Metrics.startRequestTimer();

            long authorId = request.getAuthorId();

            // TODO: Fix as get book by id
            Author authorById = authorService.getAuthorById((int) authorId);
            List<Book> booksByAuthorId = bookService.findBooksByAuthorId(authorById.getAuthor_id()).stream().toList();

            AuthorEntity authorEntity = AuthorEntity.newBuilder()
                    .setAuthorId(authorById.getAuthor_id())
                    .setName(new String(authorById.getName()))
                    .setBiography(authorById.getBiography())
                    .setAvatarUrl(authorById.getAvatar_url())
                    .build();

            List<io.clh.bookstore.author.Book> collect = booksByAuthorId.stream().map(DtoProtoConversions::convertToBookStoreAuthorBookProto).toList();

            GetAuthorByIdResponse response = GetAuthorByIdResponse.newBuilder()
                    .setAuthor(authorEntity)
                    .addAllBooks(collect).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            requestTimer.observeDuration();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void setAuthorAvatarUrlById(AuthorAvatarUrlRequest request, StreamObserver<AuthorEntity> responseObserver) {
        long authorId = request.getAuthorId();
        String avatarUrl = request.getAvatarUrl();

        try {
            Metrics.incrementTotalRequests();
            Histogram.Timer requestTimer = Metrics.startRequestTimer();

            Author author = authorService.setUrlAvatar(avatarUrl, (int) authorId);

            AuthorEntity response = AuthorEntity.newBuilder()
                    .setAuthorId(author.getAuthor_id())
                    .setName(new String(author.getName()))
                    .setBiography(author.getBiography())
                    .setAvatarUrl(author.getAvatar_url())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            requestTimer.observeDuration();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
