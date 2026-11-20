# Spring Boot Reactive MongoDB CRUD

## Overview

This Proof of Concept (PoC) demonstrates how to build a **fully reactive, non-blocking RESTful API** using Spring Boot, Spring WebFlux, Spring Data Reactive MongoDB, and Project Reactor.

The application implements CRUD (Create, Read, Update, Delete) operations for managing products stored in MongoDB.

Unlike traditional Spring MVC applications, which commonly use blocking request processing, this project uses reactive programming to support asynchronous, non-blocking data access.

## Technologies Used

- Java
- Spring Boot
- Spring WebFlux
- Spring Data Reactive MongoDB
- MongoDB
- Project Reactor (Mono and Flux)
- Maven
- RESTful APIs
- Lombok (optional)

## Key Features

- Reactive REST API development using Spring WebFlux.
- Non-blocking MongoDB operations using ReactiveMongoRepository.
- CRUD operations for product management.
- Reactive data processing using Mono and Flux.
- Reactive operators such as `map()`, `flatMap()`, and `switchIfEmpty()`.
- Separation of concerns using Controller, Service, and Repository layers.
- Asynchronous data access without blocking request-processing threads.

## Understanding Reactive Programming

Reactive programming is an asynchronous programming paradigm designed to process data streams and propagate changes efficiently.

Spring WebFlux integrates with Project Reactor, which provides two primary reactive types.

### Mono

`Mono<T>` represents an asynchronous computation that emits zero or one element.

Example:

```java
public Mono<Product> getProductById(String id) {
    return repository.findById(id);
}
```

### Flux

`Flux<T>` represents an asynchronous sequence that emits zero to many elements.

Example:

```java
public Flux<Product> getAllProducts() {
    return repository.findAll();
}
```

### Mono vs Flux

| Feature | Mono | Flux |
|---|---|---|
| Elements | 0 or 1 | 0 to N |
| Typical usage | Single record | Multiple records |
| Example | Find product by ID | Find all products |
| Reactive type | `Mono<Product>` | `Flux<Product>` |

## Project Architecture

The application follows a layered architecture.

```text
Client
   |
   v
REST Controller
   |
   v
Service Layer
   |
   v
Reactive Repository
   |
   v
MongoDB
```

### Responsibilities

**Controller Layer**

Handles HTTP requests and returns reactive responses using Mono or Flux.

**Service Layer**

Contains business logic and coordinates reactive operations.

**Repository Layer**

Uses Spring Data Reactive MongoDB to perform non-blocking database operations.

**MongoDB**

Stores product information as documents.

## Project Structure

Example project structure:

```text
springboot-reactive-mongodb-crud/
|
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   `-- com/example/reactivemongodb/
|   |   |       |-- controller/
|   |   |       |   `-- ProductController.java
|   |   |       |-- service/
|   |   |       |   `-- ProductService.java
|   |   |       |-- repository/
|   |   |       |   `-- ProductRepository.java
|   |   |       |-- entity/
|   |   |       |   `-- Product.java
|   |   |       `-- ReactiveMongoApplication.java
|   |   `-- resources/
|   |       `-- application.properties
|   `-- test/
|-- pom.xml
`-- README.md
```

## Maven Dependencies

Add the following dependencies to `pom.xml`.

```xml
<dependencies>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

These dependencies assume a Spring Boot Maven project with dependency management configured.

## MongoDB Configuration

Configure the MongoDB connection in `application.properties`.

```properties
spring.application.name=springboot-reactive-mongodb-crud

spring.data.mongodb.uri=mongodb://localhost:27017/reactive_product_db

server.port=8080
```

Ensure MongoDB is running before starting the application.

## Product Entity

Example MongoDB document representation:

```java
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private Double price;

    // Getters and setters
}
```

The `@Document` annotation maps the Java class to a MongoDB collection.

The `@Id` annotation identifies the document's primary key.

## Reactive Repository

Spring Data Reactive MongoDB provides `ReactiveMongoRepository` for asynchronous database access.

```java
@Repository
public interface ProductRepository
        extends ReactiveMongoRepository<Product, String> {
}
```

The repository provides reactive methods such as:

- `save()` — Create or update a document.
- `findById()` — Retrieve a document by ID.
- `findAll()` — Retrieve all documents.
- `deleteById()` — Delete a document by ID.

## Reactive CRUD Operations

### 1. Create Product

```java
public Mono<Product> createProduct(Product product) {
    return repository.save(product);
}
```

The `save()` method returns a Mono containing the saved product.

### 2. Retrieve All Products

```java
public Flux<Product> getAllProducts() {
    return repository.findAll();
}
```

The `findAll()` method returns a Flux containing the retrieved products.

### 3. Retrieve Product by ID

```java
public Mono<Product> getProductById(String id) {
    return repository.findById(id);
}
```

The method returns a Mono containing the product if it exists, or an empty Mono otherwise.

### 4. Update Product

```java
public Mono<Product> updateProduct(String id, Product updatedProduct) {

    return repository.findById(id)
            .flatMap(existingProduct -> {

                existingProduct.setName(updatedProduct.getName());
                existingProduct.setDescription(updatedProduct.getDescription());
                existingProduct.setPrice(updatedProduct.getPrice());

                return repository.save(existingProduct);
            });
}
```

The `flatMap()` operator chains the asynchronous lookup and save operations without explicitly blocking.

If the product does not exist, the returned Mono completes empty.

### 5. Delete Product

```java
public Mono<Void> deleteProduct(String id) {
    return repository.deleteById(id);
}
```

The method returns a `Mono<Void>` representing completion of the delete operation.

Note that `deleteById()` may complete successfully even when the ID does not exist.

## REST API Endpoints

The following endpoint definitions illustrate a typical Product CRUD implementation.

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create a product |
| GET | `/api/products` | Retrieve all products |
| GET | `/api/products/{id}` | Retrieve a product by ID |
| PUT | `/api/products/{id}` | Update an existing product |
| DELETE | `/api/products/{id}` | Delete a product |

### Sample Request — Create Product

**POST** `/api/products`

```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 75000.00
}
```

### Sample Response

```json
{
  "id": "64f123abc456",
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 75000.00
}
```

The actual generated ID will depend on MongoDB and the application's entity mapping.

## Running the Application

### Prerequisites

- Compatible Java JDK
- Maven
- MongoDB Community Server or MongoDB Atlas
- IDE such as IntelliJ IDEA, Eclipse, or VS Code

### Step 1: Clone the Repository

```bash
git clone https://github.com/<your-username>/springboot-reactive-mongodb-crud.git
```

### Step 2: Navigate to the Project

```bash
cd springboot-reactive-mongodb-crud
```

### Step 3: Start MongoDB

Start a local MongoDB instance or configure a MongoDB Atlas connection.

### Step 4: Build the Project

```bash
mvn clean install
```

### Step 5: Run the Application

```bash
mvn spring-boot:run
```

The application should start at:

```text
http://localhost:8080
```

### Step 6: Test the API

Use Postman or cURL to test the endpoints.

Example:

```bash
curl http://localhost:8080/api/products
```

## Understanding Reactive Operators

### map()

Transforms the value emitted by a reactive publisher.

```java
Mono<String> result = Mono.just("spring")
        .map(String::toUpperCase);
```

Result:

```text
SPRING
```

### flatMap()

Asynchronously transforms an emitted value into another Mono.

```java
Mono<Product> result = repository.findById(id)
        .flatMap(product -> repository.save(product));
```

### switchIfEmpty()

Provides an alternative publisher when the original publisher completes without emitting a value.

```java
return repository.findById(id)
        .switchIfEmpty(
                Mono.error(new RuntimeException("Product not found"))
        );
```

### doOnNext()

Performs a side effect when an element is emitted.

```java
return repository.findAll()
        .doOnNext(product ->
                System.out.println("Product: " + product.getName())
        );
```

`doOnNext()` is generally intended for side effects such as logging and diagnostics, rather than business transformations.

## Reactive vs Traditional Programming

| Traditional Spring MVC | Spring WebFlux |
|---|---|
| Commonly uses blocking APIs | Supports non-blocking APIs |
| Often uses synchronous database access | Supports reactive database access |
| Typically returns objects or collections | Returns Mono or Flux |
| Often uses thread-per-request processing | Supports event-loop-based request processing |
| Spring Data JPA | Spring Data Reactive MongoDB |

Reactive programming can improve resource utilization for workloads involving substantial I/O waiting, but does not automatically guarantee faster execution.

## Important Reactive Programming Practices

- Avoid calling `block()` in reactive request-processing code.
- Avoid blocking database drivers and HTTP clients on event-loop threads.
- Use reactive database drivers for end-to-end non-blocking I/O.
- Prefer reactive composition using `map()`, `flatMap()`, and other operators.
- Handle empty publishers and errors explicitly.
- Use appropriate backpressure-aware processing where applicable.
- Use `StepVerifier` for testing reactive streams.
- Avoid manually calling `subscribe()` inside typical WebFlux controllers and services.

## Learning Objectives

This PoC helps demonstrate:

1. Fundamentals of reactive programming in Java.
2. Developing REST APIs using Spring WebFlux.
3. Working with Mono and Flux.
4. Performing reactive MongoDB CRUD operations.
5. Understanding non-blocking database access.
6. Composing asynchronous operations using Reactor operators.
7. Structuring reactive applications using layered architecture.
8. Understanding the differences between Spring MVC and Spring WebFlux.

## Potential Future Enhancements

- Add global exception handling.
- Implement input validation.
- Add pagination and filtering.
- Implement reactive unit and integration tests.
- Integrate Testcontainers for MongoDB integration testing.
- Add Spring Security with JWT authentication.
- Add Docker and Docker Compose support.
- Introduce structured logging and observability.
- Add Swagger/OpenAPI documentation.

## Conclusion

This project demonstrates the fundamentals of developing reactive RESTful APIs using **Spring Boot, Spring WebFlux, Project Reactor, and MongoDB**.

It provides a foundation for understanding asynchronous data processing, reactive repository operations, and non-blocking application architecture.

## License

This project is intended for educational and Proof of Concept purposes. No specific license is declared unless a LICENSE file is added to the repository.
