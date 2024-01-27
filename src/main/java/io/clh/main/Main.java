package io.clh.main;

import io.clh.bookstore.author.AuthorService;
import io.clh.bookstore.author.AuthorServiceGrpcImp;
import io.clh.config.HibernateConfigUtil;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    // TODO: MAKE ENV
    private final static Integer GRPC_SERVER_PORT = 8082;

    public static void main(String[] args) throws IOException, InterruptedException {
        AuthorService authorService = new AuthorService(HibernateConfigUtil.createSessionFactory());
        AuthorServiceGrpcImp service = new AuthorServiceGrpcImp(authorService);

        Server server = ServerBuilder.forPort(GRPC_SERVER_PORT)
                .addService(service)
                .build();

        server.start();
        System.out.printf("Server started on port %s", GRPC_SERVER_PORT);
        server.awaitTermination();
    }
}