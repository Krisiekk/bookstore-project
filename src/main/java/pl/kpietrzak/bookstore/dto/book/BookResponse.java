package pl.kpietrzak.bookstore.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private boolean available;
}
