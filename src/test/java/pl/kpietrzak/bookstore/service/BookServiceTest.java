package pl.kpietrzak.bookstore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.kpietrzak.bookstore.dto.book.BookRequest;
import pl.kpietrzak.bookstore.dto.book.BookResponse;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.exception.BookNotFoundException;
import pl.kpietrzak.bookstore.mapper.BookMapper;
import pl.kpietrzak.bookstore.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private BookMapper bookMapper;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        bookMapper = new BookMapper();
        bookService = new BookService(bookRepository, bookMapper);
    }

    @Test
    void shouldReturnAllBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setIsbn("9780132350884");
        book.setDescription("Book about clean code");
        book.setAvailable(true);

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponse> result = bookService.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
        assertThat(result.get(0).getAuthor()).isEqualTo("Robert C. Martin");

        verify(bookRepository).findAll();
    }

    @Test
    void shouldReturnBookById() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setIsbn("9780134685991");
        book.setDescription("Java best practices");
        book.setAvailable(true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.getBookById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Effective Java");

        verify(bookRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id: 999");

        verify(bookRepository).findById(999L);
    }

    @Test
    void shouldCreateBook() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Architecture");
        request.setAuthor("Robert C. Martin");
        request.setIsbn("9780134494166");
        request.setDescription("Architecture book");

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Clean Architecture");
        savedBook.setAuthor("Robert C. Martin");
        savedBook.setIsbn("9780134494166");
        savedBook.setDescription("Architecture book");
        savedBook.setAvailable(true);

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponse result = bookService.createBook(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Clean Architecture");
        assertThat(result.isAvailable()).isTrue();

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldUpdateBook() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Old title");
        existingBook.setAuthor("Old author");
        existingBook.setIsbn("old-isbn");
        existingBook.setDescription("Old description");
        existingBook.setAvailable(true);

        BookRequest request = new BookRequest();
        request.setTitle("New title");
        request.setAuthor("New author");
        request.setIsbn("new-isbn");
        request.setDescription("New description");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        BookResponse result = bookService.updateBookBy(1L, request);

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getAuthor()).isEqualTo("New author");
        assertThat(result.getIsbn()).isEqualTo("new-isbn");
        assertThat(result.getDescription()).isEqualTo("New description");

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void shouldDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBookBy(1L);

        verify(bookRepository).existsById(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNotExistingBook() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.deleteBookBy(999L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book not found with id: 999");

        verify(bookRepository).existsById(999L);
        verify(bookRepository, never()).deleteById(999L);
    }
}