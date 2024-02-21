package io.clh.bookstore.untils;

import io.clh.bookstore.entities.Entities;
import io.clh.models.Author;

public class GrpcEntitiesToModels {
    public static Author AuthorGrpcToAuthorModel(Entities.AuthorEntity authorEntity) {
        return Author.builder()
                .avatar_url(authorEntity.getAvatarUrl())
                .biography(authorEntity.getBiography())
                .name(authorEntity.getName().toCharArray())
                .build();
    }
}
