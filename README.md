# Bookstore REST API

Bookstore REST API to backendowa aplikacja do zarządzania książkami i rezerwacjami. Projekt umożliwia rejestrację użytkowników, logowanie z użyciem JWT, kontrolę dostępu na podstawie ról `USER` i `ADMIN`, zarządzanie książkami oraz obsługę rezerwacji.

## Główne Funkcjonalności

- Rejestracja użytkowników
- Logowanie użytkowników
- Stateless JWT authentication
- Role-Based Access Control z rolami `USER` i `ADMIN`
- Publiczne przeglądanie i wyszukiwanie książek
- CRUD książek dostępny dla administratora
- Rezerwowanie książek przez zalogowanych użytkowników
- Przeglądanie własnych rezerwacji
- Zarządzanie wszystkimi rezerwacjami przez administratora
- Zmiana statusu rezerwacji przez administratora
- Dokumentacja Swagger/OpenAPI
- Baza danych PostgreSQL
- Migracje bazy danych Flyway
- Testy jednostkowe i testy kontrolerów
- Raport pokrycia testami JaCoCo powyżej 80% dla logiki aplikacji
- Strategy Pattern i polimorfizm w module powiadomień

## Technologie

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Maven
- Docker Compose
- Swagger / OpenAPI przez Springdoc
- JUnit 5
- Mockito
- JaCoCo

## Architektura Aplikacji

Aplikacja korzysta z architektury warstwowej:

```text
Controller -> Service -> Repository -> Database
DTO -> Mapper -> Entity
```

- `Controller` obsługuje żądania HTTP i wystawia endpointy REST.
- `Service` zawiera logikę biznesową aplikacji.
- `Repository` komunikuje się z bazą danych przez Spring Data JPA.
- `DTO` definiuje modele wejściowe i wyjściowe API.
- `Mapper` konwertuje DTO na encje oraz encje na DTO odpowiedzi.
- `Entity` reprezentuje tabele w bazie danych.

## Role i Uprawnienia

Projekt definiuje dwie role: `USER` oraz `ADMIN`.

`USER` może przeglądać książki, rezerwować książki oraz przeglądać swoje rezerwacje.

`ADMIN` ma uprawnienia użytkownika oraz dodatkowo może tworzyć, edytować i usuwać książki, przeglądać wszystkie rezerwacje oraz zmieniać statusy rezerwacji.

| Akcja | Publiczne | USER | ADMIN |
| --- | --- | --- | --- |
| Rejestracja | Tak | Tak | Tak |
| Logowanie | Tak | Tak | Tak |
| Przeglądanie książek | Tak | Tak | Tak |
| Wyszukiwanie książek | Tak | Tak | Tak |
| Dodanie książki | Nie | Nie | Tak |
| Edycja książki | Nie | Nie | Tak |
| Usunięcie książki | Nie | Nie | Tak |
| Rezerwacja książki | Nie | Tak | Tak |
| Przeglądanie własnych rezerwacji | Nie | Tak | Tak |
| Przeglądanie wszystkich rezerwacji | Nie | Nie | Tak |
| Zmiana statusu rezerwacji | Nie | Nie | Tak |

## Security i JWT

Aplikacja używa bezstanowego uwierzytelniania JWT. Po poprawnym logowaniu użytkownik otrzymuje token JWT. Endpointy chronione wymagają przekazania tokena w nagłówku `Authorization`:

```text
Authorization: Bearer <token>
```

Reguły dostępu są skonfigurowane w klasie `SecurityConfig`. `JwtAuthenticationFilter` odczytuje token z przychodzącego żądania, waliduje go, ładuje użytkownika i ustawia uwierzytelnienie w `SecurityContext` Spring Security.

## Endpointy API

### Auth

| Metoda | URL | Dostęp | Opis |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Publiczny | Rejestracja nowego użytkownika |
| `POST` | `/api/auth/login` | Publiczny | Logowanie i otrzymanie tokena JWT |

### Books

| Metoda | URL | Dostęp | Opis |
| --- | --- | --- | --- |
| `GET` | `/api/books` | Publiczny | Pobranie wszystkich książek |
| `GET` | `/api/books/{id}` | Publiczny | Pobranie książki po id |
| `GET` | `/api/books/search/title?title=...` | Publiczny | Wyszukiwanie książek po tytule |
| `GET` | `/api/books/search/author?author=...` | Publiczny | Wyszukiwanie książek po autorze |
| `POST` | `/api/books` | ADMIN | Dodanie książki |
| `PUT` | `/api/books/{id}` | ADMIN | Aktualizacja książki |
| `DELETE` | `/api/books/{id}` | ADMIN | Usunięcie książki |

### Reservations

| Metoda | URL | Dostęp | Opis |
| --- | --- | --- | --- |
| `POST` | `/api/reservations/books/{bookId}` | USER / ADMIN | Rezerwacja książki |
| `GET` | `/api/reservations/my` | USER / ADMIN | Pobranie rezerwacji aktualnego użytkownika |
| `GET` | `/api/reservations` | ADMIN | Pobranie wszystkich rezerwacji |
| `PATCH` | `/api/reservations/{reservationId}/status` | ADMIN | Zmiana statusu rezerwacji |

## Swagger / OpenAPI

Swagger UI jest dostępny pod adresem:

```text
http://localhost:8080/swagger-ui.html
```

Swagger pozwala przeglądać i testować endpointy API. Dla endpointów chronionych należy użyć przycisku `Authorize` i wkleić token JWT.

## ERD / Schemat Bazy Danych

![ERD diagram](docs/erd.png)

Diagram ERD przedstawia tabele `users`, `books` i `reservations` oraz relacje między nimi. Jeden użytkownik może mieć wiele rezerwacji. Jedna książka może występować w wielu rezerwacjach. Każda rezerwacja należy dokładnie do jednego użytkownika i jednej książki.

## Migracje Flyway

Schemat bazy danych jest tworzony przez migracje Flyway. Plik migracji znajduje się w:

```text
src/main/resources/db/migration/V1__init_database.sql
```

Flyway wykonuje migracje automatycznie przy starcie aplikacji. Hibernate dodatkowo waliduje zgodność encji ze schematem bazy danych.

## Wzorzec Projektowy i Polimorfizm

Projekt wykorzystuje wzorzec Strategy Pattern w module `notification`.

- `NotificationStrategy` jest interfejsem strategii.
- `ConsoleNotificationStrategy` i `EmailNotificationStrategy` są różnymi implementacjami strategii.
- `NotificationService` zależy od interfejsu `NotificationStrategy`, a nie od konkretnej klasy.

Dzięki temu sposób wysyłania powiadomień można zmienić bez zmieniania logiki rezerwacji. Rozwiązanie pokazuje też polimorfizm, ponieważ różne implementacje strategii mogą być używane przez ten sam interfejs.

## Uruchamianie Lokalne

Wymagania:

- Java 21
- Maven albo Maven Wrapper
- Docker

Uruchom PostgreSQL:

```bash
docker compose up -d
```

Uruchom aplikację Spring Boot lokalnie:

```bash
./mvnw spring-boot:run
```

Adres aplikacji:

```text
http://localhost:8080
```

Adres Swaggera:

```text
http://localhost:8080/swagger-ui.html
```

## Docker Compose

Obecny plik `docker-compose.yml` uruchamia kontener PostgreSQL używany przez aplikację.

```bash
docker compose up -d
```

Aplikacja Spring Boot jest obecnie uruchamiana lokalnie przez Maven i łączy się z PostgreSQL pod adresem `localhost:5432`.

## Domyślne Konto Administratora

Aplikacja tworzy domyślne konto administratora przy starcie, jeśli takie konto jeszcze nie istnieje:

```text
username: admin
password: admin123
role: ADMIN
```

## Testy

Projekt zawiera:

- Testy serwisów
- Testy kontrolerów
- Testy mapperów
- Testy security
- Testy modułu notification
- Testy globalnej obsługi wyjątków

Uruchomienie testów:

```bash
./mvnw clean test
```

Projekt jest skonfigurowany pod Javę 21. Przy uruchamianiu testów na nowszym JDK mogą wystąpić problemy kompatybilności agentów Mockito albo JaCoCo. Oczekiwanym środowiskiem testowym jest JDK 21.

## JaCoCo Coverage

JaCoCo generuje raport pokrycia testami podczas fazy testów Maven:

```bash
./mvnw clean test
```

Lokalizacja raportu:

```text
target/site/jacoco/index.html
```

Aktualny wygenerowany raport pokazuje pokrycie powyżej 80% dla mierzonej logiki aplikacji.

## Wykluczenia JaCoCo

Z raportu pokrycia wykluczono klasy techniczne i boilerplate:

- Klasy DTO
- Encje
- Enumy
- Klasy konfiguracyjne
- Główną klasę startową Spring Boot

Pokrycie jest liczone dla kodu zawierającego logikę aplikacji, czyli między innymi dla serwisów, kontrolerów, mapperów, security, obsługi wyjątków i powiadomień.

## Struktura Projektu

```text
src/main/java/pl/kpietrzak/bookstore
├── config        # Konfiguracja aplikacji, OpenAPI i security
├── controller    # Kontrolery REST
├── dto           # Obiekty request/response API
├── entity        # Encje JPA
├── enums         # Role i statusy rezerwacji
├── exception     # Wyjątki i globalna obsługa błędów
├── mapper        # Mapowanie DTO i encji
├── notification  # Strategy Pattern dla powiadomień
├── repository    # Repozytoria Spring Data JPA
├── security      # JWT i UserDetailsService
└── service       # Logika biznesowa
```

## Przykładowe Requesty

### Rejestracja Użytkownika

```json
{
  "username": "user1",
  "email": "user1@test.pl",
  "password": "secret123"
}
```

### Logowanie

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Dodanie Książki

```json
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "description": "Book about clean code"
}
```

### Zmiana Statusu Rezerwacji

Dostępne statusy to `PENDING`, `ACCEPTED`, `REJECTED` i `CANCELLED`.

```json
{
  "status": "ACCEPTED"
}
```

## Status Projektu

Projekt implementuje wymagane funkcjonalności backendowe: REST API, JWT authentication, RBAC, PostgreSQL, Flyway, Swagger, testy, raport JaCoCo, Strategy Pattern, polimorfizm oraz dokumentację projektu.
