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
  - eg REST API using controllers (graphql API and Websockets API)
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

now we have jdbc bean available in our config and we can use this to query the database

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






