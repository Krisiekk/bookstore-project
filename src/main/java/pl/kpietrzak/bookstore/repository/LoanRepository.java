package pl.kpietrzak.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Loan;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.LoanStatus;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndStatus(Book book, LoanStatus status);

    List<Loan> findByUser(User user);
}
