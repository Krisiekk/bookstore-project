# Bookstore REST API

Bookstore REST API is a backend application for managing books and book reservations. The project provides user registration, JWT-based authentication, role-based access control with `USER` and `ADMIN` roles, book management, and reservation management.

## Main Features

- User registration and login
- Stateless JWT authentication
- Role-Based Access Control with `USER` and `ADMIN`
- Public book browsing and searching
- Admin-only book CRUD operations
- Book reservations for authenticated users
- Viewing own reservations
- Admin access to all reservations
- Admin reservation status updates
- Swagger/OpenAPI documentation
- PostgreSQL database
- Flyway database migrations
- Unit tests and controller tests
- JaCoCo test coverage report above 80% for application logic
- Strategy Pattern and polymorphism in the notification module

## Technologies

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Maven
- Docker Compose
- Swagger / OpenAPI via Springdoc
- JUnit 5
- Mockito
- JaCoCo

## Architecture

The application uses a layered architecture:

```text
Controller -> Service -> Repository -> Database
DTO -> Mapper -> Entity
```

- `Controller` handles HTTP requests and exposes REST endpoints.
- `Service` contains business logic.
- `Repository` communicates with the database through Spring Data JPA.
- `DTO` classes define API input and output models.
- `Mapper` converts DTO objects to entities and entities to DTO responses.
- `Entity` classes represent database tables.

## Roles and Permissions

The project defines two roles: `USER` and `ADMIN`.

`USER` can browse books, reserve books, and view their own reservations.

`ADMIN` has user permissions and can additionally create, update, and delete books, view all reservations, and update reservation statuses.

| Action | Public | USER | ADMIN |
| --- | --- | --- | --- |
| Register | Yes | Yes | Yes |
| Login | Yes | Yes | Yes |
| View books | Yes | Yes | Yes |
| Search books | Yes | Yes | Yes |
| Create book | No | No | Yes |
| Update book | No | No | Yes |
| Delete book | No | No | Yes |
| Reserve book | No | Yes | Yes |
| View own reservations | No | Yes | Yes |
| View all reservations | No | No | Yes |
| Update reservation status | No | No | Yes |

## Security and JWT

The application uses stateless JWT authentication. After successful login, the user receives a JWT token. Protected endpoints require the token in the `Authorization` header:

```text
Authorization: Bearer <token>
```

Access rules are configured in `SecurityConfig`. The `JwtAuthenticationFilter` reads the token from incoming requests, validates it, loads the user, and sets authentication in Spring Security's `SecurityContext`.

## API Endpoints

### Auth

| Method | URL | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Log in and receive a JWT token |

### Books

| Method | URL | Access | Description |
| --- | --- | --- | --- |
| `GET` | `/api/books` | Public | Get all books |
| `GET` | `/api/books/{id}` | Public | Get a book by id |
| `GET` | `/api/books/search/title?title=...` | Public | Search books by title |
| `GET` | `/api/books/search/author?author=...` | Public | Search books by author |
| `POST` | `/api/books` | ADMIN | Create a book |
| `PUT` | `/api/books/{id}` | ADMIN | Update a book |
| `DELETE` | `/api/books/{id}` | ADMIN | Delete a book |

### Reservations

| Method | URL | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/reservations/books/{bookId}` | USER / ADMIN | Reserve a book |
| `GET` | `/api/reservations/my` | USER / ADMIN | Get current user's reservations |
| `GET` | `/api/reservations` | ADMIN | Get all reservations |
| `PATCH` | `/api/reservations/{reservationId}/status` | ADMIN | Update reservation status |

## Swagger / OpenAPI

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

Swagger can be used to inspect and test API endpoints. For protected endpoints, use the `Authorize` button and provide a JWT token.

## Database ERD

![ERD diagram](docs/erd.png)

The ERD diagram shows the `users`, `books`, and `reservations` tables and their relationships. One user can have many reservations. One book can be referenced by many reservations. Each reservation belongs to exactly one user and one book.

## Flyway Migrations

The database schema is created by Flyway migrations. Migration files are located in:

```text
src/main/resources/db/migration/V1__init_database.sql
```

Flyway runs automatically on application startup and validates the database schema together with Hibernate.

## Design Pattern and Polymorphism

The project uses the Strategy Pattern in the `notification` module.

- `NotificationStrategy` is the strategy interface.
- `ConsoleNotificationStrategy` and `EmailNotificationStrategy` are different implementations.
- `NotificationService` depends on the `NotificationStrategy` interface instead of a concrete implementation.

This allows the notification mechanism to be changed without changing reservation business logic. It also demonstrates polymorphism, because different strategy implementations can be used through the same interface.

## Running Locally

Requirements:

- Java 21
- Maven or Maven Wrapper
- Docker

Start PostgreSQL:

```bash
docker compose up -d
```

Run the Spring Boot application locally:

```bash
./mvnw spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

Swagger URL:

```text
http://localhost:8080/swagger-ui.html
```

## Docker Compose

The current `docker-compose.yml` starts the PostgreSQL database container used by the application.

```bash
docker compose up -d
```

The Spring Boot application is currently run locally with Maven and connects to PostgreSQL on `localhost:5432`.

## Default Administrator Account

The application creates a default administrator account on startup if it does not already exist:

```text
username: admin
password: admin123
role: ADMIN
```

## Tests

The project contains:

- Service tests
- Controller tests
- Mapper tests
- Security tests
- Notification tests
- Exception handler tests

Run tests with:

```bash
./mvnw clean test
```

The project is configured for Java 21. If tests are run on a newer JDK, Mockito or JaCoCo agent compatibility issues may occur. Use JDK 21 for the expected test environment.

## JaCoCo Coverage

JaCoCo generates a test coverage report during the Maven test phase:

```bash
./mvnw clean test
```

Report location:

```text
target/site/jacoco/index.html
```

The current generated report shows coverage above 80% for the measured application logic.

## JaCoCo Exclusions

The coverage report excludes technical and boilerplate classes:

- DTO classes
- Entity classes
- Enum classes
- Configuration classes
- Main Spring Boot application class

Coverage is focused on code containing application logic, including services, controllers, mappers, security, exception handling, and notifications.

## Project Structure

```text
src/main/java/pl/kpietrzak/bookstore
├── config        # Application, OpenAPI and security configuration
├── controller    # REST controllers
├── dto           # API request and response objects
├── entity        # JPA entities
├── enums         # Roles and reservation statuses
├── exception     # Custom exceptions and global error handling
├── mapper        # DTO and entity mapping
├── notification  # Strategy Pattern for notifications
├── repository    # Spring Data JPA repositories
├── security      # JWT and user details logic
└── service       # Business logic
```

## Example Requests

### Register User

```json
{
  "username": "user1",
  "email": "user1@test.pl",
  "password": "secret123"
}
```

### Login

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Create Book

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "description": "Book about clean code"
}
```

### Update Reservation Status

Allowed statuses are `PENDING`, `ACCEPTED`, `REJECTED`, and `CANCELLED`.

```json
{
  "status": "ACCEPTED"
}
```

## Project Status

The project implements the required backend functionality: REST API, JWT authentication, RBAC, PostgreSQL persistence, Flyway migrations, Swagger documentation, tests, JaCoCo coverage reporting, Strategy Pattern, polymorphism, and project documentation.
