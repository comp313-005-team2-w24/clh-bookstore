package io.clh.bookstore.author;

import io.clh.bookstore.book.BookService;
import io.clh.bookstore.entities.Entities;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import static io.clh.bookstore.untils.GrpcEntitiesToModels.AuthorGrpcToAuthorModel;
import static io.clh.bookstore.untils.ModelsToGrpcEntities.AuthorEntityModelToAuthorGrpc;

@RequiredArgsConstructor
public class AuthorServiceGrpcImp extends AuthorServiceGrpc.AuthorServiceImplBase {
    private final AuthorServiceImp authorServiceImp;
    private final BookService bookService;


    @Override
    public void createAuthor(Author.CreateAuthorRequest request, StreamObserver<Entities.AuthorEntity> responseObserver) {
        try {
            io.clh.models.Author author = authorServiceImp.addAuthor(AuthorGrpcToAuthorModel(request.getAuthor()));
            Entities.AuthorEntity response = AuthorEntityModelToAuthorGrpc(author);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void getAllAuthors(Author.GetAllAuthorsRequest request, StreamObserver<Entities.AuthorEntity> responseObserver) {
        super.getAllAuthors(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void getAuthorById(Author.AuthorByIdRequest request, StreamObserver<Author.GetAuthorByIdResponse> responseObserver) {
        super.getAuthorById(request, responseObserver);
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void setAuthorAvatarUrlById(Author.AuthorAvatarUrlRequest request, StreamObserver<Entities.AuthorEntity> responseObserver) {
        super.setAuthorAvatarUrlById(request, responseObserver);
    }
}
