package pl.kpietrzak.bookstore.mapper;

import org.springframework.stereotype.Component;
import pl.kpietrzak.bookstore.dto.book.BookRequest;
import pl.kpietrzak.bookstore.dto.book.BookResponse;
import pl.kpietrzak.bookstore.entity.Book;

@Component

public class BookMapper {
    public Book toEntity(BookRequest bookRequest) {
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setDescription(bookRequest.getDescription());
        book.setAvailable(true);
        return book;

    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.isAvailable()


        );
    }

    public void updateEntity(Book book, BookRequest bookRequest) {
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setDescription(bookRequest.getDescription());
    }


}
