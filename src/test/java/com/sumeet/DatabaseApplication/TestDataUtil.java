package com.sumeet.DatabaseApplication;

import com.sumeet.DatabaseApplication.domain.Author;
import com.sumeet.DatabaseApplication.domain.Book;

public class TestDataUtil {
    private TestDataUtil(){
    }

    public static Author createTestAuthor() {
        return Author.builder()
                .id(1L)
                .name("Sumeet Suryawanshi")
                .age(24)
                .build();
    }

    public static Book createTestBook() {
        return Book.builder()
                .isbn("978-1-2345-6789-0")
                .title("The Shadow in the Attic")
                .authorId(1L)
                .build();
    }
}
