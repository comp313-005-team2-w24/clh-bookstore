package io.clh.main;

import io.clh.bookstore.author.AuthorService;
import io.clh.bookstore.author.AuthorServiceGrpcImp;
import io.clh.bookstore.book.BookService;
import io.clh.bookstore.book.BookServiceGrpcImp;
import io.clh.config.HibernateConfigUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    private final static int GRPC_SERVER_PORT = Integer.parseInt(System.getenv().getOrDefault("GRPC_SERVER_PORT", "8082"));

    public static void main(String[] args) throws IOException, InterruptedException {
        /*
         * Hibernate services used for implement CRUD operations Postgresql
         */
        AuthorService authorService = new AuthorService(HibernateConfigUtil.createSessionFactory());
        BookService bookService = new BookService(HibernateConfigUtil.createSessionFactory());

        /*
         * gRPC server. use addService() to add new services (e.g. CRUD operations)
         */
        AuthorServiceGrpcImp authorServiceGrpcImp = new AuthorServiceGrpcImp(authorService);
        BookServiceGrpcImp bookServiceGrpcImp = new BookServiceGrpcImp(bookService, authorService);

        Server server = ServerBuilder.forPort(GRPC_SERVER_PORT).addService(authorServiceGrpcImp).addService(bookServiceGrpcImp).build();

        server.start();
        System.out.printf("Server started on port %s", GRPC_SERVER_PORT);
        server.awaitTermination();
    }
}