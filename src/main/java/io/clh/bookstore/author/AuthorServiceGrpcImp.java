package io.clh.bookstore.author;

import io.clh.models.Author;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.List;

public class AuthorServiceGrpcImp extends AuthorServiceGrpc.AuthorServiceImplBase {
    private final AuthorService authorService;

    public AuthorServiceGrpcImp(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public void createAuthor(CreateAuthorRequest request, StreamObserver<AuthorEntity> responseObserver) {
        /*
         * Pay attention to package name
         * io.clh.models.Author is entity, while
         * io.clh.bookstore.author.Author is from gRPC
         */

        try {
            io.clh.models.Author author = new Author();
            author.setName(request.getName().toCharArray());
            author.setBiography(request.getBiography());
            author.setAvatar_url(request.getAvatarUrl());

            Author createdAuthor = authorService.addAuthor(author);
            AuthorEntity response = AuthorEntity.newBuilder().setAuthorId(createdAuthor.getAuthor_id()).setName(Arrays.toString(createdAuthor.getName())).setBiography(createdAuthor.getBiography()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllAuthors(GetAllAuthorsRequest request, StreamObserver<AuthorEntity> responseObserver) {
        try {
            List<Author> authors = authorService.getAllAuthors();

            for (Author author : authors) {
                AuthorEntity response = AuthorEntity.newBuilder().setAuthorId(author.getAuthor_id()).setName(new String(author.getName())).setBiography(author.getBiography()).build();

                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAuthorById(AuthorByIdRequest request, StreamObserver<AuthorEntity> responseObserver) {
        try {
            long authorId = request.getAuthorId();
            Author authorById = authorService.getAuthorById((int) authorId);

            AuthorEntity response = AuthorEntity.newBuilder().setAuthorId(authorById.getAuthor_id()).setName(new String(authorById.getName())).setBiography(authorById.getBiography()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void setAuthorAvatarUrlById(AuthorAvatarUrlRequest request, StreamObserver<AuthorEntity> responseObserver) {
        long authorId = request.getAuthorId();
        String avatarUrl = request.getAvatarUrl();

        try {
            Author author = authorService.setUrlAvatar(avatarUrl, (int) authorId);

            AuthorEntity response = AuthorEntity.newBuilder()
                    .setAuthorId(author.getAuthor_id())
                    .setName(new String(author.getName()))
                    .setBiography(author.getBiography())
                    .setAvatarUrl(author.getAvatar_url())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
