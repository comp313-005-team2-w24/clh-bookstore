package io.clh.bookstore.author;

import io.clh.models.Author;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class AuthorServiceGrpcImp extends AuthorServiceGrpc.AuthorServiceImplBase {
    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void createAuthor(CreateAuthorRequest request, StreamObserver<CreateAuthorResponse> responseObserver) {
        super.createAuthor(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void getAllAuthors(GetAllAuthorsRequest request, StreamObserver<GetAllAuthorsResponse> responseObserver) {
        super.getAllAuthors(request, responseObserver);
    }
}
