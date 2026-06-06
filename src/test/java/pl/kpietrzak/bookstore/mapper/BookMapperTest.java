package pl.kpietrzak.bookstore.mapper;

import org.junit.jupiter.api.Test;
import pl.kpietrzak.bookstore.dto.book.BookRequest;
import pl.kpietrzak.bookstore.dto.book.BookResponse;
import pl.kpietrzak.bookstore.entity.Book;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    private final BookMapper bookMapper = new BookMapper();

    @Test
    void shouldMapBookRequestToBookEntity() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthor("Robert C. Martin");
        request.setIsbn("9780132350884");
        request.setDescription("Book about clean code");

        Book book = bookMapper.toEntity(request);

        assertThat(book.getTitle()).isEqualTo("Clean Code");
        assertThat(book.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(book.getIsbn()).isEqualTo("9780132350884");
        assertThat(book.getDescription()).isEqualTo("Book about clean code");
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    void shouldMapBookEntityToBookResponse() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("9780134685991");
        book.setDescription("Java best practices");
        book.setAvailable(true);

        BookResponse response = bookMapper.toResponse(book);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Effective Java");
        assertThat(response.getAuthor()).isEqualTo("Joshua Bloch");
        assertThat(response.getIsbn()).isEqualTo("9780134685991");
        assertThat(response.getDescription()).isEqualTo("Java best practices");
        assertThat(response.isAvailable()).isTrue();
    }

    @Test
    void shouldUpdateExistingBookEntity() {
        Book book = new Book();
        book.setTitle("Old title");
        book.setAuthor("Old author");
        book.setIsbn("old-isbn");
        book.setDescription("Old description");

        BookRequest request = new BookRequest();
        request.setTitle("New title");
        request.setAuthor("New author");
        request.setIsbn("new-isbn");
        request.setDescription("New description");

        bookMapper.updateEntity(book, request);

        assertThat(book.getTitle()).isEqualTo("New title");
        assertThat(book.getAuthor()).isEqualTo("New author");
        assertThat(book.getIsbn()).isEqualTo("new-isbn");
        assertThat(book.getDescription()).isEqualTo("New description");
    }
}