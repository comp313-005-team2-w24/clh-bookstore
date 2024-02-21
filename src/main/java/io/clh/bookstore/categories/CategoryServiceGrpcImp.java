package io.clh.bookstore.categories;

import com.google.protobuf.Empty;
import io.clh.bookstore.category.CategoryOuterClass;
import io.clh.bookstore.category.CategoryServiceGrpc;
import io.clh.bookstore.untils.DtoProtoConversions;
import io.clh.models.Book;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class CategoryServiceGrpcImp extends CategoryServiceGrpc.CategoryServiceImplBase {

    private final CategoryService categoryService;

    public CategoryServiceGrpcImp(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void getAllBooksByCategory(CategoryOuterClass.GetAllBooksByCategoryRequest request, StreamObserver<CategoryOuterClass.GetAllBooksByCategoryResponse> responseObserver) {
        try {
            List<Book> books = categoryService.GetAllBooksByCategory(request.getCategoryId());

            List<CategoryOuterClass.Book> list = books.stream().map(DtoProtoConversions::convertToCategoryOuterClassBookProto).toList();
            CategoryOuterClass.GetAllBooksByCategoryResponse response = CategoryOuterClass.GetAllBooksByCategoryResponse.newBuilder().addAllBooks(list).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addCategory(CategoryOuterClass.Category request, StreamObserver<CategoryOuterClass.Category> responseObserver) {
        super.addCategory(request, responseObserver);
    }

    @Override
    public void deleteCategory(CategoryOuterClass.DeleteCategoryRequest request, StreamObserver<CategoryOuterClass.Category> responseObserver) {
        super.deleteCategory(request, responseObserver);
    }

    @Override
    public void getAllCategories(Empty request, StreamObserver<CategoryOuterClass.GetAllCategoriesResponse> responseObserver) {
        super.getAllCategories(request, responseObserver);
    }

    @Override
    public void updateCategory(CategoryOuterClass.Category request, StreamObserver<CategoryOuterClass.Category> responseObserver) {
        super.updateCategory(request, responseObserver);
    }

    @Override
    public void getCategoryById(CategoryOuterClass.GetCategoryByIdRequest request, StreamObserver<CategoryOuterClass.Category> responseObserver) {
        super.getCategoryById(request, responseObserver);
    }
}
