package pl.kpietrzak.bookstore.dto.book;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 256, message = "Title must be less than 256 characters")
    private String title;

    @NotBlank(message = "Authot is required")
    @Size(max = 256, message="Author must be less than 256 characters")
    private String author;

    @Size(max=50, message="ISBN must be less than 50 characters")
    private String isbn;

    private String description;


}
