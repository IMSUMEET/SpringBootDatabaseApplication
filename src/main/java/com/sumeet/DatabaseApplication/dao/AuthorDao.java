package com.sumeet.DatabaseApplication.dao;

import com.sumeet.DatabaseApplication.domain.Author;

import java.util.Optional;

public interface AuthorDao {
    void create(Author author);

    Optional<Author> findOne(long l);

}
