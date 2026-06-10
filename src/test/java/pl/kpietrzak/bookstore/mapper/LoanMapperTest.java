package pl.kpietrzak.bookstore.mapper;

import org.junit.jupiter.api.Test;
import pl.kpietrzak.bookstore.dto.loan.LoanResponse;
import pl.kpietrzak.bookstore.entity.Book;
import pl.kpietrzak.bookstore.entity.Loan;
import pl.kpietrzak.bookstore.entity.Reservation;
import pl.kpietrzak.bookstore.entity.User;
import pl.kpietrzak.bookstore.enums.LoanStatus;
import pl.kpietrzak.bookstore.enums.Role;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoanMapperTest {

    private final LoanMapper loanMapper = new LoanMapper();

    @Test
    void shouldMapLoanToResponse() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@test.pl");
        user.setRole(Role.USER);

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Clean Code");

        Reservation reservation = new Reservation();
        reservation.setId(3L);

        LocalDateTime borrowedAt = LocalDateTime.of(2026, 6, 10, 10, 0);
        LocalDateTime returnedAt = LocalDateTime.of(2026, 6, 11, 10, 0);

        Loan loan = new Loan();
        loan.setId(4L);
        loan.setReservation(reservation);
        loan.setUser(user);
        loan.setBook(book);
        loan.setBorrowedAt(borrowedAt);
        loan.setReturnedAt(returnedAt);
        loan.setStatus(LoanStatus.RETURNED);

        LoanResponse response = loanMapper.toResponse(loan);

        assertThat(response.getId()).isEqualTo(4L);
        assertThat(response.getReservationId()).isEqualTo(3L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getBookId()).isEqualTo(2L);
        assertThat(response.getBookTitle()).isEqualTo("Clean Code");
        assertThat(response.getBorrowedAt()).isEqualTo(borrowedAt);
        assertThat(response.getReturnedAt()).isEqualTo(returnedAt);
        assertThat(response.getStatus()).isEqualTo(LoanStatus.RETURNED);
    }
}
