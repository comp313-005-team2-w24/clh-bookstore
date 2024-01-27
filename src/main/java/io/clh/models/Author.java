package io.clh.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@RequiredArgsConstructor()
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Data
@Table(name = "authors")
public class Author {
    @Id
    private int author_id;

    private char[] name;
    private String biography;
}
