package com.sumeet.DatabaseApplication;

import com.sumeet.DatabaseApplication.domain.Author;
import com.sumeet.DatabaseApplication.domain.Book;
import org.junit.jupiter.api.DisplayName;

@DisplayName("TestDataUtil")
public class TestDataUtil {
    private TestDataUtil(){
    }

    public static Author createTestAuthorA() {
        return Author.builder()
                .id(1L)
                .name("Sumeet Suryawanshi")
                .age(23)
                .build();
    }

    public static Author createTestAuthorB() {
        return Author.builder()
                .id(2L)
                .name("Akash Rana")
                .age(29)
                .build();
    }

    public static Author createTestAuthorC() {
        return Author.builder()
                .id(3L)
                .name("Sadanand Srinivasana")
                .age(24)
                .build();
    }

    public static Author createTestAuthorD() {
        return Author.builder()
                .id(4L)
                .name("Rohan Mathur")
                .age(23)
                .build();
    }

    public static Book createTestBookA() {
        return Book.builder()
                .isbn("978-1-2345-6789-0")
                .title("The Shadow in the Attic")
                .authorId(1L)
                .build();
    }

    public static Book createTestBookB() {
        return Book.builder()
                .isbn("978-1-2345-6789-1")
                .title("Beyond the horizon")
                .authorId(1L)
                .build();
    }

    public static Book createTestBookC() {
        return Book.builder()
                .isbn("978-1-2345-6789-2")
                .title("The Last Ember")
                .authorId(1L)
                .build();
    }

    public static Book createTestBookD() {
        return Book.builder()
                .isbn("978-1-2345-6789-3")
                .title("Atomic Habits")
                .authorId(1L)
                .build();
    }
}
