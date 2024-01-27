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
    public void createAuthor(CreateAuthorRequest request, StreamObserver<CreateAuthorResponse> responseObserver) {
        /*
         * Pay attention to package name
         * io.clh.models.Author is entity, while
         * io.clh.bookstore.author.Author is from gRPC
         */

        try {
            io.clh.models.Author author = new Author();
            author.setName(request.getName().toCharArray());
            author.setBiography(request.getBiography());

            Author createdAuthor = authorService.addAuthor(author);

            CreateAuthorResponse response = CreateAuthorResponse.newBuilder().setAuthorId(createdAuthor.getAuthor_id()).setName(Arrays.toString(createdAuthor.getName())).setBiography(createdAuthor.getBiography()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllAuthors(GetAllAuthorsRequest request, StreamObserver<GetAllAuthorsResponse> responseObserver) {
        try {
            List<Author> authors = authorService.getAllAuthors();
            for (Author author : authors) {
                GetAllAuthorsResponse response = GetAllAuthorsResponse.newBuilder()
                        .setAuthorId(author.getAuthor_id())
                        .setName(new String(author.getName()))
                        .setBiography(author.getBiography())
                        .build();

                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
