package pl.kpietrzak.bookstore.service;

import org.springframework.stereotype.Service;
import pl.kpietrzak.bookstore.dto.book.BookRequest;
import pl.kpietrzak.bookstore.dto.book.BookResponse;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.exception.BookNotFoundException;
import pl.kpietrzak.bookstore.mapper.BookMapper;
import pl.kpietrzak.bookstore.repository.BookRepository;

import  java.util.List;

/**
 * Service responsible for book management business logic.
 */
@Service
public class BookService {

    private  final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookResponse> getAllBooks() {
        return  bookRepository.findAll().stream().map(bookMapper::toResponse).toList();

    }

    public BookResponse getBookById(Long id) {
        Book book = findBookEntityById(id);
        return bookMapper.toResponse(book);

    }

    public List<BookResponse> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream().map(bookMapper::toResponse).toList();

    }

    public List<BookResponse> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream().map(bookMapper::toResponse).toList();
    }

    public BookResponse createBook(BookRequest request) {
        Book book = bookMapper.toEntity(request);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);

    }

    public BookResponse updateBookBy(Long id, BookRequest request) {
        Book book = findBookEntityById(id);
        bookMapper.updateEntity(book, request);
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponse(updatedBook);

    }


    public void deleteBookBy(Long id) {
        if(!bookRepository.existsById(id)){
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    private Book findBookEntityById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

}
