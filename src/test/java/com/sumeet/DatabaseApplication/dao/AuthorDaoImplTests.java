package com.sumeet.DatabaseApplication.dao;

import com.sumeet.DatabaseApplication.dao.impl.AuthorDaoImpl;
import com.sumeet.DatabaseApplication.domain.Author;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthorDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;


    @InjectMocks
    private AuthorDaoImpl underTest;

    @Test
    public void testThatCreateAuthorGeneratesCorrectSql(){
//      Creating author with values then using
        Author author = Author.builder()
                .id(1L)
                .name("Sumeet Suryawanshi")
                .age(24)
                .build();

//      .create to insert into database
        underTest.create(author);

        verify(jdbcTemplate).update(
                eq("INSERT INTO authors (id, name, age) VALUES (?, ?, ?)"),
                eq(1L),
                eq("Sumeet Suryawanshi"),
                eq(24)
        );
    }
}
