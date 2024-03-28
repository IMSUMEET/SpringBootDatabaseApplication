package com.sumeet.DatabaseApplication.dao;

import com.sumeet.DatabaseApplication.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    void create(Book book);

    Optional<Book> find(String isbn);

    List<Book> find();

    void update(String isbn, Book bookA);

    void delete(String isbn);
}
