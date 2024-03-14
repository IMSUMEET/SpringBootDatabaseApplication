package com.sumeet.DatabaseApplication.dao.impl;

import com.sumeet.DatabaseApplication.dao.BookDao;
import com.sumeet.DatabaseApplication.domain.Book;
import org.springframework.jdbc.core.JdbcTemplate;

public class BookDaoImpl implements BookDao {
    private final JdbcTemplate jdbcTemplate;

    public BookDaoImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Book book) {
        jdbcTemplate.update(
            "INSERT INTO books (isbn, title, authorId) VALUES (?, ?, ?)",
            book.getIsbn(),
            book.getTitle(),
            book.getAuthorId());
    }
}
