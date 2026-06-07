package pl.kpietrzak.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.kpietrzak.bookstore.dto.book.BookRequest;
import pl.kpietrzak.bookstore.dto.book.BookResponse;
import pl.kpietrzak.bookstore.service.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnAllBooks() throws Exception {
        BookResponse response = new BookResponse(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Book about clean code",
                true
        );

        when(bookService.getAllBooks()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));

        verify(bookService).getAllBooks();
    }

    @Test
    void shouldReturnBookById() throws Exception {
        BookResponse response = new BookResponse(
                1L,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                "Java best practices",
                true
        );

        when(bookService.getBookById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"));

        verify(bookService).getBookById(1L);
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Architecture");
        request.setAuthor("Robert C. Martin");
        request.setIsbn("9780134494166");
        request.setDescription("Architecture book");

        BookResponse response = new BookResponse(
                1L,
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                "Architecture book",
                true
        );

        when(bookService.createBook(any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Architecture"));

        verify(bookService).createBook(any(BookRequest.class));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Updated title");
        request.setAuthor("Updated author");
        request.setIsbn("updated-isbn");
        request.setDescription("Updated description");

        BookResponse response = new BookResponse(
                1L,
                "Updated title",
                "Updated author",
                "updated-isbn",
                "Updated description",
                true
        );

        when(bookService.updateBookBy(eq(1L), any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated title"));

        verify(bookService).updateBookBy(eq(1L), any(BookRequest.class));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBookBy(1L);
    }

    @Test
    void shouldSearchBooksByTitle() throws Exception {
        BookResponse response = new BookResponse(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Book about clean code",
                true
        );

        when(bookService.searchBooksByTitle("Clean")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/books/search/title")
                        .param("title", "Clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"));

        verify(bookService).searchBooksByTitle("Clean");
    }

    @Test
    void shouldSearchBooksByAuthor() throws Exception {
        BookResponse response = new BookResponse(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Book about clean code",
                true
        );

        when(bookService.searchBooksByAuthor("Martin")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/books/search/author")
                        .param("author", "Martin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));

        verify(bookService).searchBooksByAuthor("Martin");
    }
}