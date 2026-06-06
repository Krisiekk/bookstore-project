package pl.kpietrzak.bookstore.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.kpietrzak.bookstore.dto.book.BookRequest;
import pl.kpietrzak.bookstore.dto.book.BookResponse;
import pl.kpietrzak.bookstore.service.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/search/title")
    public List<BookResponse> getBooksByTitle(@RequestParam String title) {
        return bookService.searchBooksByTitle(title);
    }

    @GetMapping("/search/author")
    public List<BookResponse> getBooksByAuthor(@RequestParam String author) {
        return bookService.searchBooksByAuthor(author);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody BookRequest bookRequest) {
        return bookService.createBook(bookRequest);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest bookRequest) {

        return bookService.updateBookBy(id, bookRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteBookBy(id);
    }
}
