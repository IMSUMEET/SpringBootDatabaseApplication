# SpringBootBasics
basics about spring-boot 

### mvn commands

⁠ bash
./mvnw compile -> compiles source code into byte code

./mvnw test -> runs unit tests

./mvnw package -> creates jar and war files

./mvnw verify -> runs checks and integration tests
 ⁠
### these command run in sequence

  ie if you run ./mvnw package it will first run ./mvnw compile and ./mvnw test
  ----------

## Running spring boot project (2 ways)

1.⁠ ⁠Create jar file and run using terminal
⁠ bash
   ./mvnw verify
    cd target
    java -jar <filename>.jar
 ⁠

2.⁠ ⁠run using spring boot run
⁠ bash
./mvnw spring-boot:run
 ⁠
-----
## Layers
1.Persistance Layer
  - handles all interactions with persistance technology like databases.
  - exposed via interfaces
  - Entities -> Repositories/ DAO (data access objects) -> Databases

2.Service Layer
  - uses all the functionality exposed by the persitance layer
  - avoid talking between presentation and persistance layer directly with each other

3.Presentation Layer
  - take all data given by service layer and expose that to users.
  - e.g. REST API using controllers (graphql API and Websockets API)
------
## Dependency Injection

a technique whereby one object (or static method) supplies the dependencies of another object.


## Configuration files

1. Running on a different port
    - inside src/main/resources/application properties.
    ```bash
    server.port=8181
    ```

    Tomcat started on port 8181 (http) with context path ''

    - inside src/main/resources/application.yml
    ```bash
    server:
      port: 8282
    ```

    Tomcat started on port 8282 (http) with context path ''


    - we can also have configuration files (.properties or .yml in test directory)

## Environment Variables

1. Configuring environment variables via IntelliJ
  - configuration -> edit configuration -> environment variables 
  - write SERVER_PORT=8181

2. Configuring environment variables via Command line
  a. via maven spring boot plugin
  ```bash
    SERVER_PORT=8282 ./mvnw spring-boot:run
  ```

  b. exporting SERVER_PORT to env before running application
  ```bash
    export SERVER_PORT=8383
    ./mvnw spring-boot:run
  ```

## Custom Configuration from file

PizzaConfig.java file under config directory under src

```bash
@NoArgsConstructor
@Data
public class PizzaConfig {
    private String sauce;
    private String topping;
    private String crust;

}
```

application.properties

```bash
pizza.sauce=bbq
pizza.topping=chicken
pizza.crust=stuffed
```

PizzaApplication.java

```bash
package com.sumeet.quickstart;

import com.sumeet.quickstart.config.PizzaConfig;
import com.sumeet.quickstart.services.ColourPrinter;
import com.sumeet.quickstart.services.Impl.ColourPrinterImpl;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log
public class PizzaApplication implements CommandLineRunner {

	private PizzaConfig pizzaConfig;

	public PizzaApplication(PizzaConfig pizzaConfig){
		this.pizzaConfig = pizzaConfig;
	}

	public static void main(String[] args){
		SpringApplication.run(PizzaApplication.class, args);
	}

	@Override
	public void run(final String... argsg){

	log.info(
		String.format("I want %s crust pizza with %s and its %s sauce",
		pizzaConfig.getCrust(),
		pizzaConfig.getTopping(),
		pizzaConfig.getSauce()
		));	
	}
}
```

##   **starting in memory h2 database** 

```bash
package com.sumeet.databaseh2;

import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
@Log
public class Databaseh2Application implements CommandLineRunner {

	private final DataSource datasource;

	public Databaseh2Application(final DataSource dataSource){
		this.datasource = dataSource;
	}

	public static void main(String[] args) {
		SpringApplication.run(Databaseh2Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Datasource " + datasource.toString());
		final JdbcTemplate restTemplate = new JdbcTemplate(datasource);
		restTemplate.execute("select 1");
	}
}


```
HikariPool-1 - Starting...
HikariPool-1 - Added connection conn0:url=jdbc:h2:mem:65204de9-84bc-4f7d-a14c-8f2c836521e9 user=SA

### what actually happens inside auto setup of in memory database with application properties

application.properties
```bash
spring.application.name=databaseh2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=SA
spring.datasource.password=password
```

## Connecting to a real Database (PostgreSQL database)


dependency

```bash
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

application.properties

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=sumeetisbest!
spring.datasource.driver-class-name=org.postgresql.Driver
```

docker-compose.yml

```bash
version: '3.1'

services:

  db:
    image: postgres
    ports:
      - "5432:5432"
    restart: always
    environment:
      POSTGRES_PASSWORD: sumeetisbest!
```

To run:

```bash
docker-compose up
```

```bash
./mvnw spring-boot:run
```


## Initializing DB Schema

schema.sql

```bash
DROP TABLE IF EXISTS "widgets"

DROP SEQUENCE IF EXISTS widgets_id_seq;
CREATE SEQUENCE widgets_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE;

CREATE TABLE "widgets" (
    "id" bigint DEFAULT nextval ('widget_id_seq') NOT NULL,
    "name" text,
    "purpose" text,
    CONSTRAINT "widget_pkev" PRIMARY KEY ("id")
);
```

data.sql

```bash
INSERT INTO widget (id, name, purpose) VALUES
(1, 'Widget A', 'Used for testing purposes.'),
(2, 'Widget B', 'Used for testing purposes.'),
(3, 'Widget C', 'Used for testing purposes.'),
(4, 'Widget D', 'Used for testing purposes.'),
(5, 'Widget E', 'Used for testing purposes.');
```

After creating .sql files for schema add another line in application.properties along with the previous code i.e.,
spring.sql.init.mode=always

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=sumeetisbest!
spring.datasource.driver-class-name=org.postgresql.Driver

spring.sql.init.mode=always
```

This will create a table and insert values when application is run

## JDBC Template Setup

In order to get the JDBC template we will make a configuration class inside config directory

```bash
package com.sumeet.databaseh2.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
```

now we have jdbc bean available in our config, and we can use this to query the database

### DAO

Data access object allows the seperation between service layer and persistance layer, provides modularity, ease of conversion from java object to sql code and remove potential duplicate code.

### Setup DAO

create 2 packages inside main.java.com.sumeet.DatabaseApplication

```bash
dao
    - impl
        - AuthorDaoImpl.class
        - BookDaoImpl.class
    AuthorDao.interface
    BookDao.interface
domain
    - Author.class
    - Book.class

```

Create schema.sql inside resources folder


Inside test create resources and application.properties to do the integration testing of h2 in memory database

```bash
    test
      java
      resources
        - application.properties
```
 
run the test application that connects to h2 in memory database based on information provided under application properties inside test/resources directory

### Create DAO

we have everything setup for integration tests

lets do unit tests first

create new file AuthorDaoImplTests to test that class.
now we know that this class will be using jdbc template but we are writing a unit test rather than an integration tests therefore lets use mockito.

test/java/com.sumeet.DatabaseApplication/dao

AuthorDaoImplTests

```bash
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

```

BookDaoImplTests

```bash
package com.sumeet.DatabaseApplication.dao;

import com.sumeet.DatabaseApplication.dao.impl.BookDaoImpl;
import com.sumeet.DatabaseApplication.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class BookDaoImplTests {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BookDaoImpl underTest;

    @Test
    public void testThatCreateBookGeneratesCorrectSql(){

        Book book = Book.builder()
                .isbn("978-1-2345-6789-0")
                .title("The Shadow in the Attic")
                .authorId(1L)
                .build();

        underTest.create(book);

        verify(jdbcTemplate).update(
            eq("INSERT INTO books (isbn, title, authorId) VALUES (?, ?, ?)"),
            eq("978-1-2345-6789-0"),
            eq("The Shadow in the Attic"),
            eq(1L)
        );
    }

}

```

### Read One

#### --------- Authors ------------

AuthorDaoImplTests.java
```bash
@Test
public void testThatFindOneGeneratesCorrectSql(){
    underTest.findOne(1L);
    verify(jdbcTemplate).query(
            "SELECT id, name, age FROM authors WHERE id = ? LIMIT 1",
            ArgumentMatchers.<AuthorDaoImpl.AuthorRowMapper>any(),
            eq(1L)
    );

}
```

AuthorDao.java (interface)
```bash
 Optional<Author> findOne(long l);
```

AuthorDaoImpl.java (class)
```bash
@Override
public Optional<Author> findOne(long authorId) {
    List<Author> results = jdbcTemplate.query(
            "SELECT id, name, age FROM authors WHERE id = ? LIMIT 1",
            new AuthorRowMapper(), authorId);

    return results.stream().findFirst();
}

@Override
public static class AuthorRowMapper implements RowMapper<Author> {

    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Author.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .age(rs.getInt("age"))
                .build();
    }
}
```

#### ------- Books --------

BookDaoImplTests.java

```bash
@Test
public void testThatFindsBookGeneratesCorrectSql() {
    underTest.find("978-1-2345-6789-0");
    verify(jdbcTemplate).query(
            eq("SELECT isbn, title, author FROM books WHERE id = ? LIMIT 1"),
            ArgumentMatchers.<BookDaoImpl.BookRowMapper>any(),
            eq("978-1-2345-6789-0")
    );
}
```

BookDao.java (interface)
```bash
Optional<Book> find(String isbn);
```

BookDaoImpl.java (class)
```bash
@Override
public Optional<Book> find(String isbn) {
    List<Book> results =  jdbcTemplate.query(
            "SELECT isbn, title, author FROM books WHERE id = ? LIMIT 1",
            new BookRowMapper(), isbn);

    return results.stream().findFirst();
}


public static class BookRowMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Book.builder()
                .isbn(rs.getString("isbn"))
                .title(rs.getString("title"))
                .authorId(rs.getLong("authorId"))
                .build();
    }
}
```
## Creating Integration Tests

#### ----------- AUTHOR -------------

### TestDataUtil class to use the author just once for testing 

```bash
package com.sumeet.DatabaseApplication;

import com.sumeet.DatabaseApplication.domain.Author;

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
}
```

To get hold of underTest object inside AuthorDaoImplIntegrationTests.java class from AuthorDaoImplTests.java ... we use SpringBoot Constructor injection with the annotation of Autowired. Which is not required in the main production code since it is automatically configured.

Integration Tests for AuthorDaoImplementation

```bash
@Component
```
annotation on AuthorDaoImpl.class to use as injection inside AuthorDaoImplIntegrationTests.java

AuthorDaoImplIntegrationTests.java

```bash
package com.sumeet.DatabaseApplication.dao.impl;

import com.sumeet.DatabaseApplication.TestDataUtil;
import com.sumeet.DatabaseApplication.domain.Author;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AuthorDaoImplIntegrationTests {

    private AuthorDaoImpl underTest;

    @Autowired
    public AuthorDaoImplIntegrationTests(AuthorDaoImpl underTest){
        this.underTest = underTest;
    }

    @Test
    public void testThatAuthorCanBeCreatedAndRecalled(){
        Author author = TestDataUtil.createTestAuthor();

        underTest.create(author);
        Optional<Author> result =  underTest.findOne(author.getId());

        //  assertj
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(author);


    }

}

```

#### ----------- BOOK -------------

```bash
@Component
```
annotation on BookDaoImpl.class to use as injection inside BookDaoImplIntegrationTests.java

BookDaoImplIntegrationTests.java

```bash
package com.sumeet.DatabaseApplication.dao.impl;

import com.sumeet.DatabaseApplication.TestDataUtil;
import com.sumeet.DatabaseApplication.dao.AuthorDao;
import com.sumeet.DatabaseApplication.domain.Author;
import com.sumeet.DatabaseApplication.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class BookDaoImplIntegrationTests {

    private AuthorDao authorDao;
    private BookDaoImpl underTest;

    @Autowired
    public BookDaoImplIntegrationTests(BookDaoImpl underTest, AuthorDao authorDao){
        this.underTest = underTest;
        this.authorDao = authorDao;
    }

    @Test
    public void testThatBookCanBeCreatedAndRecalled(){
        Author author = TestDataUtil.createTestAuthor();
        authorDao.create(author);

        Book book = TestDataUtil.createTestBook();
        book.setAuthorId(author.getId());

        underTest.create(book);
        Optional<Book> result =  underTest.find(book.getIsbn());

        //  assertj
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(book);

    }
}
```

AuthorDao was needed in the above test file just to satisfy a foreign key constraint for the Books table

###  Find Many 

#### ----------- Author -------------

AuthorDaoImpleTests.java
```bash
@Test
public void testThatFindManyGeneratesCorrectSql(){
    underTest.find();
    verify(jdbcTemplate).query(
            eq("SELECT id, name, age FROM authors"),
            ArgumentMatchers.<AuthorDaoImpl.AuthorRowMapper>any()
    );
}
```

AuthorDao (interface)
```bash
List<Author> find();
```

AuthorDaoImpl.java
```bash
@Override
public List<Author> find() {
    return jdbcTemplate.query("SELECT id, name, age FROM authors",
            new AuthorRowMapper()
    );
}
```

AuthorDaoImplIntegrationTests.java
```bash
@Test
public void testThatMultipleAuthorsCanBeCreatedAndRecalled(){
    Author authorA = TestDataUtil.createTestAuthorA();
    underTest.create(authorA);
    Author authorB = TestDataUtil.createTestAuthorB();
    underTest.create(authorB);
    Author authorC = TestDataUtil.createTestAuthorC();
    underTest.create(authorC);
    Author authorD = TestDataUtil.createTestAuthorD();
    underTest.create(authorD);


    List<Author> result = underTest.find();
    assertThat(result)
            .hasSize(4)
            .containsExactly(authorA, authorB, authorC, authorD);
}
```

#### ----------- Book -------------

BookDaoImplTests.java
```bash
@Test
public void testThatFindGeneratesCorrectSql(){
    underTest.find();
    verify(jdbcTemplate).query(
            eq("SELECT isbn, title, author_id FROM books"),
            ArgumentMatchers.<BookDaoImpl.BookRowMapper>any()
    );
}
```

BookDao (interface)
```bash
List<Book> find();
```

BookDaoImpl.java
```bash
@Override
public List<Book> find() {
    return jdbcTemplate.query(
            "SELECT isbn, title, author_id FROM books",
            new BookRowMapper()
    );
}
```

BookDaoImplIntegrationTests.java
```bash
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
```
Add above annotation above class to clean database after every test method to remove tests pollution

```bash
 @Test
public void testThatMultipleBooksCanBeCreatedAndRecalled(){
    Author author = TestDataUtil.createTestAuthorA();
    authorDao.create(author);

    Book bookA = TestDataUtil.createTestBookA();
    bookA.setAuthorId(author.getId());
    underTest.create(bookA);

    Book bookB = TestDataUtil.createTestBookB();
    bookB.setAuthorId(author.getId());
    underTest.create(bookB);

    Book bookC = TestDataUtil.createTestBookC();
    bookC.setAuthorId(author.getId());
    underTest.create(bookC);

    Book bookD = TestDataUtil.createTestBookD();
    bookD.setAuthorId(author.getId());
    underTest.create(bookD);

    List<Book> results = underTest.find();
    assertThat(results).hasSize(4).containsExactly(bookA, bookB, bookC, bookD);
}
```

TestDataUtil.java
```bash
package com.sumeet.DatabaseApplication;

import com.sumeet.DatabaseApplication.domain.Author;
import com.sumeet.DatabaseApplication.domain.Book;

public class TestDataUtil {
    private TestDataUtil(){
    }

    public static Author createTestAuthorA() {
        return Author.builder()
                .id(1L)
                .name("Sumeet Suryawanshi")
                .age(24)
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
```






