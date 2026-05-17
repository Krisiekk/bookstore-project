package pl.kpietrzak.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kpietrzak.bookstore.entity.Book;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByUser(String title);

    List<Book> findAllByAuthor(String author);



}
