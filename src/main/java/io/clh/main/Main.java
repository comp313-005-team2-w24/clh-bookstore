package io.clh.main;

import io.clh.bookstore.author.AuthorServiceGrpcImp;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    // TODO: MAKE ENV
    private final static Integer GRPC_SERVER_PORT = 8082;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(GRPC_SERVER_PORT)
                .addService(new AuthorServiceGrpcImp())
                .build();

        server.start();
        System.out.println("Server started, listening on " + GRPC_SERVER_PORT);

        server.awaitTermination();
    }
}