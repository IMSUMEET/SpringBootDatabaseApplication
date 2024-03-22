package com.sumeet.DatabaseApplication.dao;

import com.sumeet.DatabaseApplication.domain.Book;

import java.util.Optional;

public interface BookDao {
    void create(Book book);

    Optional<Book> find(String isbn);
}
